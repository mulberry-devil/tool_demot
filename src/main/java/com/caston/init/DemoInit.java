package com.caston.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caston.hot_search.service.HotSearchService;
import com.caston.send_mail.entity.Alioss;
import com.caston.send_mail.enums.ALiOSSEnum;
import com.caston.send_mail.service.AliossService;
import com.caston.wechat.entity.Wechat;
import com.caston.wechat.entity.WechatTemplate;
import com.caston.wechat.enums.WeChatEnum;
import com.caston.wechat.service.WechatService;
import com.caston.wechat.service.WechatTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * <p>
 * 初始化热点数据
 * </p>
 *
 * @author caston
 * @since 2022-08-03
 */
@Component
public class DemoInit {

    private static final Logger log = LoggerFactory.getLogger(DemoInit.class);

    @Autowired
    private HotSearchService hotSearchService;
    @Autowired
    private AliossService aliossService;
    @Autowired
    private WechatService wechatService;
    @Autowired
    WechatTemplateService wechatTemplateService;

    @PostConstruct
    public void init() {
        log.info("项目启动，开始初始化阿里云oss......");
        Alioss alioss = aliossService.getOne(null);
        ALiOSSEnum.ENDPOINT.setAliField(alioss.getEndpoint());
        ALiOSSEnum.ACCESSKEYID.setAliField(alioss.getAccesskeyid());
        ALiOSSEnum.ACCESSKEYSECRET.setAliField(alioss.getAccesskeysecret());
        log.info("开始初始化热点数据......");
        try {
            hotSearchService.addHotSearchInit("weibo", "https://tenapi.cn/resou/");
            hotSearchService.addHotSearchInit("zhihu", "https://tenapi.cn/zhihuresou/");
            hotSearchService.addHotSearchInit("douyin", "https://tenapi.cn/douyinresou/");
            hotSearchService.addHotSearchInit("blibli", "https://tenapi.cn/bilihot/");
            hotSearchService.addHotSearchInit("baidu", "https://tenapi.cn/baiduhot/");
            log.info("热点数据初始化结束......");
        } catch (Exception e) {
            log.error("初始化数据发生异常：", e);
        }
        log.info("开始初始化微信开发数据......");
        try {
            Wechat wechat = wechatService.getOne(new LambdaQueryWrapper<Wechat>().eq(Wechat::getStatus, 1).eq(Wechat::getType, "wechat"));
            WeChatEnum.APPID.setAliField(wechat.getAppid());
            WeChatEnum.APPSECRET.setAliField(wechat.getAppsecret());
            WechatTemplate template = wechatTemplateService.getOne(new LambdaQueryWrapper<WechatTemplate>().eq(WechatTemplate::getStatue, 1));
            WeChatEnum.TEMPLATEID.setAliField(template.getTemplateid());
            Wechat wether = wechatService.getOne(new LambdaQueryWrapper<Wechat>().eq(Wechat::getStatus, 1).eq(Wechat::getType, "wether"));
            WeChatEnum.CITY_URL.setAliField(WeChatEnum.CITY_URL.getAliField().replace("MYKEY", wether.getAppsecret()));
            WeChatEnum.WEATHER_URL.setAliField(WeChatEnum.WEATHER_URL.getAliField().replace("MYKEY", wether.getAppsecret()));
            WeChatEnum.TEXT_URL.setAliField(WeChatEnum.TEXT_URL.getAliField().replace("MYKEY", wether.getAppsecret()));
        } catch (Exception e) {
            log.error("初始化数据发生异常：", e);
        }
    }
}
