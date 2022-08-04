package com.caston.send_mail.mq.handler;

import com.caston.send_mail.entity.MailVo;
import com.caston.send_mail.mq.consumer.MailConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MailHandler {
    private static final Logger log = LoggerFactory.getLogger(MailHandler.class);

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private MailConsumer mailConsumer;

    @RabbitListener(queues = "${mail.queue.name}", containerFactory = "singleListenerContainer")
    public void onMessage(@Payload byte[] message) throws Exception {
        // log.info("mail监听消费用户日志 监听到消息： {} ", message);
        MailVo mailVo = objectMapper.readValue(message, MailVo.class);
        // log.info("mail监听消费用户日志 监听到消息： {} ", mailVo);
        mailConsumer.send(mailVo);
    }
}
