package com.caston.wechat.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.caston.common.result.Response;
import com.caston.quartz.TaskConstants;
import com.caston.quartz.entity.Task;
import com.caston.quartz.service.TaskService;
import com.caston.quartz.utils.SnowflakeIdWorkerUtil;
import com.caston.wechat.entity.*;
import com.caston.wechat.service.WechatMessageService;
import com.caston.wechat.service.WechatNoteService;
import com.caston.wechat.service.WechatService;
import com.caston.wechat.service.WechatUserService;
import com.caston.wechat.utils.WeChatUtil;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author caston
 * @since 2022-08-27
 */
@RestController
@RequestMapping("/wechat")
public class WechatController {

    private static final Logger log = LoggerFactory.getLogger(WechatController.class);

    @Autowired
    private WechatService wechatService;
    @Autowired
    private WechatUserService wechatUserService;
    @Autowired
    private WechatNoteService wechatNoteService;
    @Autowired
    private WechatMessageService messageService;
    @Autowired
    private TaskService taskService;

    @PostMapping("/send")
    @RequiresRoles(value = {"manager"}, logical = Logical.OR)
    public Response send() {
        List<WechatUser> list = new ArrayList<>();
        wechatUserService.list().stream().forEach(i -> {
            try {
                log.info("开始给{}推送模板消息", i.getUserName());
                Map<String, Object> msg = wechatService.getWeather(i);
                String accessToken = wechatService.getAccessToken(i);
                wechatService.send(i, accessToken, msg);
                log.info("完成给{}推送模板消息", i.getUserName());
            } catch (Exception e) {
                log.error("给{}推送模板消息失败", i.getUserName(), e);
            }
        });
        if (list.size() != 0) {
            return Response.success(list).message("有用户推送失败");
        }
        return Response.success();
    }

    @PostMapping("/addNote")
    @RequiresRoles(value = {"manager"}, logical = Logical.OR)
    public Response addNote(@RequestParam String userName, @RequestParam String note) {
        String userId = wechatUserService.getOne(new LambdaQueryWrapper<WechatUser>().eq(WechatUser::getUserName, userName)).getUserId();
        WechatNote wechatNote = wechatNoteService.getOne(new LambdaQueryWrapper<WechatNote>().eq(WechatNote::getIsnew, 1).eq(WechatNote::getUserid, userId));
        if (wechatNote != null) {
            wechatNoteService.update(null, new LambdaUpdateWrapper<WechatNote>()
                    .eq(WechatNote::getIsnew, 1).eq(WechatNote::getUserid, userId)
                    .set(WechatNote::getNote, note).set(WechatNote::getNoteDate, new Date()));
        } else {
            wechatNoteService.save(new WechatNote(userId, note, new Date(), 1));
        }
        return Response.success();
    }

    @PostMapping("/addUser")
    @RequiresRoles(value = {"manager"}, logical = Logical.OR)
    public Response addUser(@RequestBody WechatUser wechatUser) {
        wechatUserService.save(wechatUser);
        return Response.success();
    }

    @GetMapping("messageHandle")
    @ApiIgnore
    public String wxSignatureCheck(
            @RequestParam(value = "signature") String signature,
            @RequestParam(value = "timestamp") String timestamp,
            @RequestParam(value = "nonce") String nonce,
            @RequestParam(value = "echostr") String echostr) {
        return wechatService.wxSignatureCheck(signature, timestamp, nonce, echostr);
    }

    @PostMapping("messageHandle")
    @ApiIgnore
    public void messageHandle(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("开始处理公众号接收到的消息...");
            Map<String, String> map = WeChatUtil.xml2MapFromStream(request.getInputStream());
            String msgType = map.get("MsgType");
            if ("text".equals(msgType)) {
                String content = map.get("Content");
                String userId = map.get("FromUserName");
                String createTime = map.get("CreateTime");
                String toUserName = map.get("ToUserName");
                messageService.save(new WechatMessage(userId, content, createTime));
                String[] split = content.split("：|:");
                StringBuilder builder;
                RespMessage_Text responseText = new RespMessage_Text();
                if (split.length > 1 && "提醒".equals(split[0])) {
                    WechatNote wechatNote = wechatNoteService.getOne(new LambdaQueryWrapper<WechatNote>().eq(WechatNote::getIsnew, 1).eq(WechatNote::getUserid, userId));
                    builder = new StringBuilder(split[1]);
                    if (wechatNote != null) {
                        wechatNoteService.update(null, new LambdaUpdateWrapper<WechatNote>()
                                .eq(WechatNote::getIsnew, 1).eq(WechatNote::getUserid, userId)
                                .set(WechatNote::getNote, builder.toString()).set(WechatNote::getNoteDate, new Date()));
                    } else {
                        wechatNoteService.save(new WechatNote(userId, builder.toString(), new Date(), 1));
                    }

                    //设置返回内容
                    responseText.setContent("增加提醒成功,提醒内容为：" + builder);
                    wechatService.sendMessage2Wechat(response, responseText, toUserName, userId);
                } else if (split.length > 1 && "追加提醒".equals(split[0])) {
                    WechatNote wechatNote = wechatNoteService.getOne(new LambdaQueryWrapper<WechatNote>().eq(WechatNote::getIsnew, 1).eq(WechatNote::getUserid, userId));
                    if (wechatNote != null) {
                        builder = new StringBuilder(wechatNote.getNote() + ";");
                        builder.append(split[1]);
                        wechatNoteService.update(null, new LambdaUpdateWrapper<WechatNote>()
                                .eq(WechatNote::getIsnew, 1).eq(WechatNote::getUserid, userId)
                                .set(WechatNote::getNote, builder.toString()).set(WechatNote::getNoteDate, new Date()));
                    } else {
                        builder = new StringBuilder(split[1]);
                        wechatNoteService.save(new WechatNote(userId, builder.toString(), new Date(), 1));
                    }
                    //设置返回内容
                    responseText.setContent("追加提醒成功,提醒内容为：" + builder);
                    wechatService.sendMessage2Wechat(response, responseText, toUserName, userId);
                } else if (split.length > 1 && "定时提醒".equals(split[0])) {
                    int j = split[2].indexOf("(");
                    int k = split[2].indexOf(")");
                    String time = split[2].substring(0, j);
                    String task_content = split[2].substring(j + 1, k);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[1]));
                    cal.set(Calendar.MINUTE, Integer.parseInt(time));
                    cal.set(Calendar.SECOND, 0);
                    String dateFormat = "ss mm HH dd MM ? yyyy";
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                    String cron = sdf.format(cal.getTime());
                    Task task = new Task(SnowflakeIdWorkerUtil.generateId(), TaskConstants.JOB, TaskConstants.JOB + userId, "定时任务提醒", cron, 0, TaskConstants.BEAN_CLASS, task_content, false, userId);
                    taskService.saveTask(task);
                    responseText.setContent("添加定时提醒成功");
                    wechatService.sendMessage2Wechat(response, responseText, toUserName, userId);
                }
            }
            log.info("公众号消息处理完成...");
        } catch (Exception e) {
            log.error("公众号消息处理出现异常：", e);
        }
    }
}

