package com.caston.xxljob.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caston.hot_search.service.HotSearchService;
import com.caston.send_mail.entity.MailVo;
import com.caston.send_mail.enums.ALiOSSEnum;
import com.caston.send_mail.mq.produce.MailProduce;
import com.caston.send_mail.service.MailVoService;
import com.caston.wechat.entity.Content;
import com.caston.wechat.service.WechatService;
import com.caston.wechat.service.WechatUserService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

/**
 * XxlJob开发示例（Bean模式）
 * <p>
 * 开发步骤：
 * 1、任务开发：在Spring Bean实例中，开发Job方法；
 * 2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 * 4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Component
public class HotSearchXxlJob {
    private static final Logger log = LoggerFactory.getLogger(HotSearchXxlJob.class);

    @Autowired
    private HotSearchService hotSearchService;
    @Autowired
    private MailVoService mailVoService;
    @Autowired
    private WechatService wechatService;
    @Autowired
    private WechatUserService wechatUserService;
    @Autowired
    private MailProduce mailProduce;
    @Value("${mail.aliyun.bucketName}")
    private String bucketName;

    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("hotSearchJobHandler")
    public void hotSearchJobHandler() throws Exception {
        XxlJobHelper.log("XXL-JOB, start update redis.");
        log.info("开始更新redis中数据");
        String jobParam = XxlJobHelper.getJobParam();
        JSONObject jsonObject = JSONObject.parseObject(jobParam);
        jsonObject.forEach((i, j) -> {
            hotSearchService.addHotSearch(i, (String) j);
        });
    }

    @XxlJob("dealDeadMailJobHandler")
    public void dealDeadMailJobHandler() throws Exception {
        log.info("开始执行邮件发送失败重试任务");
        OSS oss = new OSSClientBuilder().build(ALiOSSEnum.ENDPOINT.getAliField(), ALiOSSEnum.ACCESSKEYID.getAliField(), ALiOSSEnum.ACCESSKEYSECRET.getAliField());
        List<MailVo> mailVos = mailVoService.list();
        for (MailVo mailVo : mailVos) {
            List<Map<String, Object>> list = new ArrayList<>();
            String filesStr = mailVo.getFilesStr();
            String[] filenames = filesStr.split(",");
            for (String filename : filenames) {
                Map<String, Object> map = new HashMap<>();
                InputStream inputStream = oss.getObject(bucketName, filename).getObjectContent();
                byte[] fileByte = IOUtils.toByteArray(inputStream);
                map.put("fileName", filename);
                map.put("fileByte", Base64.encodeBase64String(fileByte));
                list.add(list.size(), map);
                oss.deleteObject(bucketName, filename);
                log.info("删除阿里云oss中文件（{}）", filename);
            }
            String fileMapStr = JSONObject.toJSONString(list);
            mailVoService.remove(new QueryWrapper<MailVo>().lambda().eq(MailVo::getFilesStr, filesStr));
            log.info("删除数据库中数据{}", mailVo);
            mailVo.setFilesStr(fileMapStr);
            mailProduce.sendQue(mailVo);
        }
    }

    @XxlJob("sendWechatJobHandler")
    public void sendWechatJobHandler() throws Exception {
        log.info("开始执行微信公众号推送任务");
        wechatUserService.list().stream().forEach(i -> {
            try {
                log.info("开始给{}推送模板消息", i.getUserName());
                Map<String, Object> msg = wechatService.getWeather(i);
                String accessToken = wechatService.getAccessToken(i);
                wechatService.send(i, accessToken, msg);
                log.info("完成给{}推送模板消息", i.getUserName());
            }catch (Exception e){
                log.error("给{}推送模板消息失败", i.getUserName(), e);
            }
        });
        log.info("微信公众号推送任务结束");
    }
}
