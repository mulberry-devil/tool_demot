package com.caston.quartz.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caston.send_mail.entity.MailVo;
import com.caston.send_mail.mq.produce.MailProduce;
import com.caston.wechat.entity.WechatUser;
import com.caston.wechat.service.WechatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class WeChatRemindJob {
    @Autowired
    private WechatUserService wechatUserService;
    @Autowired
    private MailProduce mailProduce;

    public void remind(String userId, String content) {
        //TODO 微信提醒
        System.out.println("模拟微信提醒------------" + userId);
        //TODO 邮箱提醒
        System.out.println("模拟邮箱提醒------------" + userId);
        WechatUser user = wechatUserService.getOne(new LambdaQueryWrapper<WechatUser>().eq(WechatUser::getUserId, userId));
        MailVo mailVo = new MailVo(user.getMail(), null, "定时提醒", content, false, null, new Date());
        mailProduce.sendQue(mailVo);
    }
}
