package com.hs.common.rabbitmq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;


/**
 * RabbitMQ的配置 消息发送到 exchange，queue 的回调函数
 **/
@Configuration
public class MsgSendConfirmCallback implements RabbitTemplate.ConfirmCallback{

    private static Logger logger = LogManager.getLogger(MsgSendConfirmCallback.class);


    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {

    }
}
