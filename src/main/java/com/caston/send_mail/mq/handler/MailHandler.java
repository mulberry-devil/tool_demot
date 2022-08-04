package com.caston.send_mail.mq.handler;

import com.caston.send_mail.entity.MailVo;
import com.caston.send_mail.mq.consumer.MailConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MailHandler {
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private MailConsumer mailConsumer;

    @RabbitListener(queues = "${mail.queue.name}", containerFactory = "singleListenerContainer")
    public void onMessage(@Payload byte[] message) throws Exception {
        MailVo mailVo = objectMapper.readValue(message, MailVo.class);
        mailConsumer.send(mailVo);
    }
}
