package com.caston.send_mail.controller;


import com.caston.send_mail.entity.SendMail;
import com.caston.send_mail.service.SendMailService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import java.util.Date;

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

    @PostMapping("/sendMail")
    @RequiresPermissions(value = {"manager:all", "user:send"}, logical = Logical.OR)
    public String sendMail(@RequestParam String to, @RequestParam(required = false) String cc, @RequestParam String subject, @RequestParam String text, @RequestParam Boolean isHtml, @RequestPart(required = false) MultipartFile[] files) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(mailSender.getUsername());
        helper.setTo(to.split(","));
        helper.setSubject(subject);
        helper.setText(text, isHtml);
        helper.setSentDate(new Date());
        if (StringUtils.isNoneBlank(cc)) {
            helper.setCc(cc.split(","));
        }
        if (files != null && files.length != 0) {
            for (MultipartFile file : files) {
                helper.addAttachment(file.getOriginalFilename(), file);
            }
        }
        mailSender.send(mimeMessage);
        return "success";
    }

    @PostMapping("/updateMail")
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

