package com.caston.send_mail.service.impl;

import com.caston.send_mail.entity.SendMail;
import com.caston.send_mail.mapper.SendMailMapper;
import com.caston.send_mail.service.SendMailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author caston
 * @since 2022-07-28
 */
@Service
public class SendMailServiceImpl extends ServiceImpl<SendMailMapper, SendMail> implements SendMailService {
    private static final Logger log = LoggerFactory.getLogger(SendMailServiceImpl.class);

    @Autowired
    private SendMailMapper sendMailMapper;

    @Override
    public void updateMail(SendMail sendMail, JavaMailSenderImpl javaMailSender) {
        log.info("开始更新数据库以及邮件配置...");
        sendMailMapper.insert(sendMail);
        javaMailSender.setHost(sendMail.getHost());
        javaMailSender.setPort(sendMail.getPort());
        javaMailSender.setUsername(sendMail.getUsername());
        javaMailSender.setPassword(sendMail.getPassword());
        log.info("更新成功...");
    }
}
