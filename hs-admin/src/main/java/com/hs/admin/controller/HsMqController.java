package com.hs.admin.controller;

import com.hs.common.rabbitmq.RabbitmqConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

@RestController
@RequestMapping("/mq")
@Slf4j
public class HsMqController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 发送延时信息
    public void sendTimeoutMsg(String exchange,String content, String routingKey, int delay) {
        // 通过广播模式发布延时消息，会广播至每个绑定此交换机的队列，这里的路由键没有实质性作用
        rabbitTemplate.convertAndSend(exchange, routingKey, content, message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            // 毫秒为单位，指定此消息的延时时长
            message.getMessageProperties().setDelay(delay * 1000);
            return message;
        });
    }
    // 发送普通消息
    public void sendMsg(String exchange,String routingKey, String content) {
        // DirectExchange类型的交换机，必须指定对应的路由键
        rabbitTemplate.convertAndSend(exchange, routingKey, content);
    }

    // 监听消费延时消息
    @RabbitListener(queues = {"delay_queue"})
    public void process(String content, Message message, Channel channel) throws IOException {
        try {
            // 消息的可定确认，第二个参数如果为true将一次性确认所有小于deliveryTag的消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            //调用方法消费消息 自己业务具体实现类
            log.info("延迟队列消息[{}]被消费！！",content);
        } catch (Exception e) {
            log.error("延迟队列消息 处理失败:{}", e.getMessage());
            // 直接拒绝消费该消息，后面的参数一定要是false，否则会重新进入业务队列，不会进入死信队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            /*// ack返回false，requeue-true并重新回到队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);*/
        }
    }

    // 消费普通消息
    @RabbitListener(queues = {"normal_queue"})
    public void process1(String content, Message message, Channel channel) throws IOException {
        try {
            log.info("普通队列的内容[{}]", content);
            // 消息的可定确认，第二个参数如果为true将一次性确认所有小于deliveryTag的消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("普通信息处理完毕");
        } catch (Exception e) {
            log.error("处理失败:{}", e.getMessage());
            // 直接拒绝消费该消息，后面的参数一定要是false，否则会重新进入业务队列，不会进入死信队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    @PostMapping(value = "/consumer")
    public void consumer(){

    }

    @PostMapping(value = "/producer")
    public void producer(){
        sendMsg(RabbitmqConstant.MQ_WEBSITE_NORMAL_EXCHANGE,RabbitmqConstant.MQ_WEBSITE_NORMAL_ROUTING_KEY,"111");
    }
}
