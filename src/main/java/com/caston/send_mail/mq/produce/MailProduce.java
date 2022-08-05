package com.caston.send_mail.mq.produce;

import com.caston.send_mail.entity.MailVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MailProduce {
    private static final Logger log = LoggerFactory.getLogger(MailProduce.class);

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private Environment environment;
    @Resource
    private ObjectMapper objectMapper;

    public void sendQue(MailVo mailVo) {
        try {
            log.info("开始发送消息进队列...");
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(environment.getProperty("mail.exchange.name"));
            rabbitTemplate.setRoutingKey(environment.getProperty("mail.routing.key.name"));
            Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(mailVo)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
            message.getMessageProperties().setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, MessageProperties.CONTENT_TYPE_JSON);
            String msg = (String) rabbitTemplate.convertSendAndReceive(message);
            log.info("消息发送成功...");
        } catch (Exception e) {
            log.error("消息发送异常：{}", e);
        }
    }
}
