package com.caston.send_mail.mq.handler;

import com.caston.send_mail.entity.MailVo;
import com.caston.send_mail.mq.consumer.MailConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class MailHandler {
    private static final Logger log = LoggerFactory.getLogger(MailHandler.class);

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private MailConsumer mailConsumer;

    @RabbitListener(queues = "${mail.queue.name}", containerFactory = "singleListenerContainer")
    public void onMessage(Message message, Channel channel) throws IOException {
        log.info("队列监听到消息，开始进行处理...");
        long tag = message.getMessageProperties().getDeliveryTag();
        byte[] hotMessage = message.getBody();
        MailVo mailVo = objectMapper.readValue(hotMessage, MailVo.class);
        mailConsumer.send(mailVo,tag,channel);
        log.info("消息处理成功...");
    }
}
