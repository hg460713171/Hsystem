package com.hs.common.rabbitmq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MsgSendReturnCallback implements RabbitTemplate.ReturnsCallback {
    private static Logger logger = LogManager.getLogger(MsgSendReturnCallback.class);

    /**
     * 使用该功能需要开启消息返回确认，yml需要配置 publisher-returns: true
     * message    消息主体 message
     * replyCode  消息主体 message
     * replyText  描述
     * exchange   消息使用的交换机
     * routingKey 消息使用的路由键
     * <p>
     * PS：通过实现ReturnCallback接口，如果消息从交换机发送到对应队列失败时触发
     * </p>
     */


    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        if (returnedMessage.getExchange().equals(RabbitmqConstant.MQ_WEBSITE_FILM_DELAY_EXCHANGE)) {
            // 如果配置了发送回调ReturnCallback，rabbitmq_delayed_message_exchange插件会回调该方法，因为发送方确实没有投递到队列上，只是在交换器上暂存，等过期/时间到了才会发往队列。
            // 所以如果是延迟队列的交换器，则直接放过，并不是bug
            return;
        }
        String correlationId = returnedMessage.getMessage().getMessageProperties().getCorrelationId();
        logger.debug("消息：{} 发送失败, 应答码：{} 原因：{} 交换机: {}  路由键: {}", correlationId, returnedMessage.getReplyCode(), returnedMessage.getReplyText(), returnedMessage.getExchange(), returnedMessage.getRoutingKey());
    }
}
