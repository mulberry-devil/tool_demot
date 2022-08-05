package com.caston.send_mail.service;

import com.caston.send_mail.entity.SendMail;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author caston
 * @since 2022-07-28
 */
public interface SendMailService extends IService<SendMail> {
    /**
     * 更新邮件配置
     *
     * @param sendMail
     * @param mailSender
     */
    public void updateMail(SendMail sendMail, JavaMailSenderImpl mailSender);
}
