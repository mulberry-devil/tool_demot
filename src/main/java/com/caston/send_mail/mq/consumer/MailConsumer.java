package com.caston.send_mail.mq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.caston.send_mail.entity.MailVo;
import com.rabbitmq.client.Channel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class MailConsumer {
    private static final Logger log = LoggerFactory.getLogger(MailConsumer.class);

    @Autowired
    private JavaMailSenderImpl mailSender;

    public String send(MailVo mailVo, Long tag, Channel channel) throws IOException {
        try {
            log.info("开始进行邮件发送处理...");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(mailSender.getUsername());
            helper.setTo(mailVo.getTo().split(","));
            helper.setSubject(mailVo.getSubject());
            helper.setText(mailVo.getText(), mailVo.getIsHtml());
            helper.setSentDate(new Date());
            if (StringUtils.isNoneBlank(mailVo.getCc())) {
                helper.setCc(mailVo.getCc().split(","));
            }
            List<Map> fileList = JSONObject.parseArray(mailVo.getFilesStr(), Map.class);
            if (fileList != null && fileList.size() > 0) {
                for (Map m : fileList) {
                    // 将string转为byte数组
                    byte[] fileByte = Base64.decodeBase64(m.get("fileByte") == null ? "" : m.get("fileByte").toString());
                    InputStream inputStream = new ByteArrayInputStream(fileByte);
                    String fileName = m.get("fileName").toString();
                    helper.addAttachment(fileName, new ByteArrayResource(IOUtils.toByteArray(inputStream)));
                }
            }
            mailSender.send(mimeMessage);
            log.info("邮件发送成功，请注意通知查收...");
            log.info("开始向队列发送确认消息...");
            channel.basicAck(tag, true);
        } catch (Exception e) {
            log.error("邮件发送失败：{}", e);
            log.info("开始重试发送邮件");
            channel.basicReject(tag,true);
        }
        return "发送成功";
    }
}
