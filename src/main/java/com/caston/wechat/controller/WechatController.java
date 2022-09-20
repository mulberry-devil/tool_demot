package com.caston.wechat.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.caston.wechat.entity.*;
import com.caston.wechat.service.WechatMessageService;
import com.caston.wechat.service.WechatNoteService;
import com.caston.wechat.service.WechatService;
import com.caston.wechat.service.WechatUserService;
import com.caston.wechat.utils.WeChatUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    @Autowired
    private WechatMessageService messageService;

    @PostMapping("/send")
    public void send() {
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

    @PostMapping("/addUser")
    public void addUser(@RequestBody WechatUser wechatUser) {
        wechatUserService.save(wechatUser);
    }

    @GetMapping("messageHandle")
    public String wxSignatureCheck(
            @RequestParam(value = "signature") String signature,
            @RequestParam(value = "timestamp") String timestamp,
            @RequestParam(value = "nonce") String nonce,
            @RequestParam(value = "echostr") String echostr) {
        return wechatService.wxSignatureCheck(signature, timestamp, nonce, echostr);
    }

    @PostMapping("messageHandle")
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
                    RespMessage_Text responseText = new RespMessage_Text();
                    //设置返回内容
                    responseText.setContent("增加提醒成功,提醒内容为：" + builder);
                    wechatService.sendMessage2Wechat(response, responseText, toUserName, userId, builder);
                } else if (split.length > 1 && "追加提醒".equals(split[0])) {
                    WechatNote wechatNote = wechatNoteService.getOne(new LambdaQueryWrapper<WechatNote>().eq(WechatNote::getIsnew, 1).eq(WechatNote::getUserid, userId));
                    if (wechatNote != null) {
                        builder = new StringBuilder(wechatNote.getNote()+";");
                        builder.append(split[1]);
                        wechatNoteService.update(null, new LambdaUpdateWrapper<WechatNote>()
                                .eq(WechatNote::getIsnew, 1).eq(WechatNote::getUserid, userId)
                                .set(WechatNote::getNote, builder.toString()).set(WechatNote::getNoteDate, new Date()));
                    } else {
                        builder = new StringBuilder(split[1]);
                        wechatNoteService.save(new WechatNote(userId, builder.toString(), new Date(), 1));
                    }
                    RespMessage_Text responseText = new RespMessage_Text();
                    //设置返回内容
                    responseText.setContent("追加提醒成功,提醒内容为：" + builder);
                    wechatService.sendMessage2Wechat(response, responseText, toUserName, userId, builder);
                }
            }
            log.info("公众号消息处理完成...");
        } catch (Exception e) {
            log.error("公众号消息处理出现异常：", e);
        }
    }
}

