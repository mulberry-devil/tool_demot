package com.caston.send_mail.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caston.send_mail.entity.SendMail;
import com.caston.send_mail.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import springfox.documentation.oas.annotations.EnableOpenApi;

import java.util.Properties;

@Configuration
@EnableOpenApi
public class ToolConfig {

    @Autowired
    private SendMailService sendMailService;

    @Bean
    public JavaMailSenderImpl javaMailSender() {
        SendMail sendMail = sendMailService.getOne(new LambdaQueryWrapper<SendMail>().eq(SendMail::getStatus, 1));
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(sendMail.getHost());
        javaMailSender.setPort(sendMail.getPort());
        javaMailSender.setUsername(sendMail.getUsername());
        javaMailSender.setPassword(sendMail.getPassword());
        javaMailSender.setDefaultEncoding("UTF-8");
        javaMailSender.setProtocol("smtp");
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.smtp.ssl.enable", "true");
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.starttls.required", "true");
        javaMailSender.setJavaMailProperties(properties);
        return javaMailSender;
    }
}
