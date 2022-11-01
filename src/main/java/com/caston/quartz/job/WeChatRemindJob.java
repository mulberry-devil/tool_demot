package com.caston.quartz.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caston.quartz.utils.BeansUtils;
import com.caston.send_mail.entity.MailVo;
import com.caston.send_mail.mq.produce.MailProduce;
import com.caston.wechat.entity.WechatUser;
import com.caston.wechat.service.WechatUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class WeChatRemindJob {
    private static final Logger log = LoggerFactory.getLogger(WeChatRemindJob.class);

    public void remind(String userId, String content) {
        //TODO 微信提醒
        log.info("开始对{}进行邮箱提醒...", userId);
        WechatUser user = BeansUtils.getBean(WechatUserService.class).getOne(new LambdaQueryWrapper<WechatUser>().eq(WechatUser::getUserId, userId));
        MailVo mailVo = new MailVo(user.getMail(), null, "定时提醒", content, false, null, new Date());
        BeansUtils.getBean(MailProduce.class).sendQue(mailVo);
    }
}
