package com.caston.wechat.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.caston.wechat.entity.Content;
import com.caston.wechat.entity.WechatNote;
import com.caston.wechat.entity.WechatUser;
import com.caston.wechat.service.WechatNoteService;
import com.caston.wechat.service.WechatService;
import com.caston.wechat.service.WechatUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
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

    private static final Logger log = LoggerFactory.getLogger(WechatController.class);

    @Autowired
    private WechatService wechatService;
    @Autowired
    private WechatUserService wechatUserService;
    @Autowired
    private WechatNoteService wechatNoteService;

    @PostMapping("/send")
    public void send() {
        wechatUserService.list().stream().forEach(i -> {
            log.info("开始给{}推送模板消息", i.getUserName());
            Map<String, Content> weather = wechatService.getWeather(i);
            String accessToken = wechatService.getAccessToken(i);
            wechatService.send(i, accessToken, weather);
            log.info("完成给{}推送模板消息", i.getUserName());
        });
    }

    @PostMapping("/addNote")
    public void addNote(@RequestParam String userName, @RequestParam String note) {
        String userId = wechatUserService.getOne(new LambdaQueryWrapper<WechatUser>().eq(WechatUser::getUserName, userName)).getUserId();
        WechatNote wechatNote = wechatNoteService.getOne(new LambdaQueryWrapper<WechatNote>().eq(WechatNote::getIsnew, 1).eq(WechatNote::getUserid, userId));
        if (wechatNote != null) {
            wechatNoteService.update(null, new LambdaUpdateWrapper<WechatNote>()
                    .eq(WechatNote::getIsnew, 1).eq(WechatNote::getUserid, userId)
                    .set(WechatNote::getNote, note).set(WechatNote::getNoteDate, new Date()));
        } else {
            wechatNoteService.save(new WechatNote(userId, note, new Date(), 1));
        }
    }
}

