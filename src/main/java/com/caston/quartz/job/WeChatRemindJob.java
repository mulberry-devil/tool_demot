package com.caston.quartz.job;

import org.springframework.stereotype.Component;

@Component
public class WeChatRemindJob {
    public void remind(String userId){
        //TODO 微信提醒
        System.out.println("模拟微信提醒------------"+userId);
        //TODO 邮箱提醒
        System.out.println("模拟邮箱提醒------------"+userId);
    }
}
