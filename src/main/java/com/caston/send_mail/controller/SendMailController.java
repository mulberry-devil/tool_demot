package com.caston.send_mail.controller;


import com.alibaba.fastjson.JSONObject;
import com.caston.send_mail.entity.MailVo;
import com.caston.send_mail.entity.SendMail;
import com.caston.send_mail.mq.produce.MailProduce;
import com.caston.send_mail.service.SendMailService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author caston
 * @since 2022-07-28
 */
@RestController
@RequestMapping("/sendMail")
@RequiresRoles(value = {"manager", "user"}, logical = Logical.OR)
public class SendMailController {
    @Autowired
    private JavaMailSenderImpl mailSender;
    @Autowired
    private SendMailService sendMailService;
    @Autowired
    private MailProduce mailProduce;

    @PostMapping("/sendMail")
    @RequiresPermissions(value = {"manager:all", "user:send"}, logical = Logical.OR)
    public String sendMail(@RequestParam String to, @RequestParam(required = false) String cc, @RequestParam String subject, @RequestParam String text, @RequestParam Boolean isHtml, @RequestPart(required = false) List<MultipartFile> files) throws Exception {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (MultipartFile f : files) {
            Map<String, Object> map = new HashMap<String, Object>();
            String filename = f.getOriginalFilename();
            byte[] fileByte = f.getBytes();
            map.put("fileName", filename);
            map.put("fileByte", Base64.encodeBase64String(fileByte));
            list.add(list.size(), map);
        }
        String fileMapStr = JSONObject.toJSONString(list);
        MailVo mailVo = new MailVo(to, cc, subject, text, isHtml, fileMapStr);
        mailProduce.sendQue(mailVo);
        return "success";
    }

    @PutMapping("/updateMail")
    @RequiresPermissions(value = {"manager:all", "user:update"}, logical = Logical.OR)
    public String updateMail(@RequestParam String host, @RequestParam String username, @RequestParam String password, Integer port) {
        sendMailService.lambdaUpdate().eq(SendMail::getStatus, 1).set(SendMail::getStatus, 0).update();
        SendMail sendMail;
        sendMail = sendMailService.lambdaQuery()
                .eq(SendMail::getHost, host)
                .eq(SendMail::getPort, port)
                .eq(SendMail::getUsername, username)
                .eq(SendMail::getPassword, password).one();
        if (sendMail != null) {
            sendMail.setStatus(1);
            sendMailService.save(sendMail);
        } else {
            sendMail = new SendMail();
            sendMail.setHost(host);
            sendMail.setPort(port);
            sendMail.setUsername(username);
            sendMail.setPassword(password);
            sendMail.setStatus(1);
            sendMailService.updateMail(sendMail, mailSender);
        }
        return "success";
    }
}

