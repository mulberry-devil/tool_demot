package com.caston.send_mail.mq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.caston.send_mail.entity.MailVo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class MailConsumer {
    @Autowired
    private JavaMailSenderImpl mailSender;

    public String send(MailVo mailVo) throws Exception {
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
        List<Map> attachList= JSONObject.parseArray(mailVo.getFilesStr(),Map.class);
        if(attachList!=null && attachList.size()>0){
            String fileid = null;
            for(Map m:attachList){
                byte[] fileByte= Base64.decodeBase64(m.get("fileByte")==null?"":m.get("fileByte").toString());
                InputStream inputStream = new ByteArrayInputStream(fileByte);
                String fileName=m.get("fileName").toString();
                helper.addAttachment(fileName,new ByteArrayResource(IOUtils.toByteArray(inputStream)));
            }
        }
        // List<MultipartFile> files = mailVo.getFiles();
        // if (files != null && files.size() != 0) {
        //     for (MultipartFile file : files) {
        //         helper.addAttachment(file.getOriginalFilename(), file);
        //     }
        // }
        mailSender.send(mimeMessage);
        return "发送成功";
    }
}
