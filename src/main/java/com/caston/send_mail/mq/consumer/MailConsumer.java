package com.caston.send_mail.mq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.caston.send_mail.entity.MailVo;
import com.caston.send_mail.enums.ALiOSSEnum;
import com.caston.send_mail.service.MailVoService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.UUID;

@Component
public class MailConsumer {
    private static final Logger log = LoggerFactory.getLogger(MailConsumer.class);

    @Autowired
    private JavaMailSenderImpl mailSender;
    @Autowired
    private MailVoService mailVoService;
    @Value("${mail.aliyun.bucketName}")
    private String bucketName;

    public Boolean send(MailVo mailVo) throws IOException {
        try {
            log.info("开始进行邮件发送处理...");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(mailSender.getUsername());
            helper.setTo(mailVo.getMailTo().split(","));
            helper.setSubject(mailVo.getSubject());
            helper.setText(mailVo.getMailText(), mailVo.getIsHtml());
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
            return true;
        } catch (Exception e) {
            log.error("邮件发送失败：{}", e);
            return false;
        }
    }

    public Boolean deadDeal(MailVo mailVo) {
        OSS oss = new OSSClientBuilder().build(ALiOSSEnum.ENDPOINT.getAliField(), ALiOSSEnum.ACCESSKEYID.getAliField(), ALiOSSEnum.ACCESSKEYSECRET.getAliField());
        List<Map> fileList = JSONObject.parseArray(mailVo.getFilesStr(), Map.class);
        StringBuilder stringBuilder = new StringBuilder();
        if (fileList != null && fileList.size() > 0) {
            for (Map m : fileList) {
                // 将string转为byte数组
                byte[] fileByte = Base64.decodeBase64(m.get("fileByte") == null ? "" : m.get("fileByte").toString());
                InputStream inputStream = new ByteArrayInputStream(fileByte);
                String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
                String[] split = m.get("fileName").toString().split("\\.");
                String fileName = split[0] + uuid + "." + split[1];
                oss.putObject(bucketName, fileName, inputStream);
                log.info("将（{}）加入到阿里云oss中...", fileName);
                stringBuilder.append(fileName + ",");
            }
        }
        oss.shutdown();
        mailVo.setFilesStr(stringBuilder.toString());
        mailVo.setMailDate(new Date());
        boolean save = false;
        save = mailVoService.save(mailVo);
        log.info("将{}存入数据库中", mailVo);
        return save;
    }
}
