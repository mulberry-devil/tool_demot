package com.caston.send_mail.mq.handler;

import com.caston.send_mail.entity.MailVo;
import com.caston.send_mail.mq.consumer.MailConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${mail.rabbitmq.max_retry}")
    private int MAX_RETRIES; //消息最大重试次数
    @Value("${mail.rabbitmq.retry_interval}")
    private int RETRY_INTERVAL; //重试间隔(秒)

    @SneakyThrows
    @RabbitListener(queues = "${mail.queue.name}", containerFactory = "singleListenerContainer")
    public void onMessage(Message message, Channel channel) throws IOException {
        log.info("队列监听到消息，开始进行处理...");
        long tag = message.getMessageProperties().getDeliveryTag();
        byte[] mailMessage = message.getBody();
        MailVo mailVo = objectMapper.readValue(mailMessage, MailVo.class);
        // 重试次数
        int retryCount = 0;
        boolean success = false;
        while (!success && retryCount < MAX_RETRIES) {
            retryCount++;
            success = mailConsumer.send(mailVo);
            if (!success) {
                String errorTip = "第" + retryCount + "次消费失败" +
                        ((retryCount < 3) ? "," + RETRY_INTERVAL + "s后重试" : ",进入死信队列");
                log.error(errorTip);
                Thread.sleep(RETRY_INTERVAL * 1000);
            }
        }
        if (success) {
            // 消费成功，确认
            channel.basicAck(tag, true);
            log.info("消息处理成功...");
        } else {
            // 重试多次之后仍失败，进入死信队列
            channel.basicNack(tag, false, false);
            log.error("消息处理失败...");
        }
    }

    @RabbitListener(queues = "${mail.dead.queue.name}", containerFactory = "singleListenerContainer")
    public void onDeadMessage(Message message, Channel channel) throws Exception {
        log.info("开始处理死信队列数据...");
        byte[] mailMessage = message.getBody();
        MailVo mailVo = objectMapper.readValue(mailMessage, MailVo.class);
        Boolean deal = mailConsumer.deadDeal(mailVo);
        if (deal) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
            log.info("消息处理成功...");
        } else {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            log.info("消息处理失败...");
        }
    }
}
