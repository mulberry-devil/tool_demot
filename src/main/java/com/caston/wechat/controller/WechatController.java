package com.caston.wechat.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caston.wechat.entity.Content;
import com.caston.wechat.entity.WechatToken;
import com.caston.wechat.enums.WeChatEnum;
import com.caston.wechat.service.WechatService;
import com.caston.wechat.service.WechatTokenService;
import com.caston.wechat.service.WechatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private WechatService wechatService;
    @Autowired
    private WechatUserService wechatUserService;

    @PostMapping("/send")
    public void send() {
        wechatUserService.list().stream().forEach(i -> {
            Map<String, Content> weather = wechatService.getWeather(i);
            String accessToken = wechatService.getAccessToken(i);
            wechatService.send(i, accessToken, weather);
        });
    }
}

