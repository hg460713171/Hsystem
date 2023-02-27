package com.hs.common.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ的配置 主要是队列，交换机的配置绑定
 **/
@Configuration
public class RabbitmqConfig {

    /**
     * durable="true" 持久化 rabbitmq重启的时候不需要创建新的队列
     * exclusive 表示该消息队列是否只在当前connection生效,默认是false
     * auto-delete 表示消息队列没有在使用时将被自动删除 默认是false
     */
    // 延时队列
    @Bean
    public Queue delayQueue() {
        return new Queue(RabbitmqConstant.MQ_WEBSITE_FILM_QUEUE, true,false,false);
    }

    /**
     * 交换机说明:
     * durable="true" rabbitmq重启的时候不需要创建新的交换机
     * auto-delete 表示交换机没有在使用时将被自动删除 默认是false
     * direct交换器相对来说比较简单，匹配规则为：如果路由键匹配，消息就被投送到相关的队列
     * topic交换器你采用模糊匹配路由键的原则进行转发消息到队列中
     * fanout交换器中没有路由键的概念，他会把消息发送到所有绑定在此交换器上面的队列中。
     */
    // 延时交换机 设置
    @Bean
    public FanoutExchange delayExchange() {
        //使用自定义交换器
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        FanoutExchange fanoutExchange = new FanoutExchange(RabbitmqConstant.MQ_WEBSITE_FILM_DELAY_EXCHANGE, true, false, args);
        fanoutExchange.setDelayed(true);
        return fanoutExchange;
    }

    // 绑定延时队列与延时交换机
    @Bean
    public Binding delayBind() {
        return BindingBuilder.bind(delayQueue()).to(delayExchange());
    }

    // ------------------------普通队列------------------------
    // 普通队列
    @Bean
    public Queue normalQueue() {
        return new Queue(RabbitmqConstant.MQ_WEBSITE_NORMAL_QUEUE, true);
    }

    // 普通交换机
    @Bean
    public DirectExchange normalExchange() {
        return new DirectExchange(RabbitmqConstant.MQ_WEBSITE_NORMAL_EXCHANGE, true, false);
    }

    // 绑定普通消息队列
    @Bean
    public Binding normalBind() {
        return BindingBuilder.bind(normalQueue()).to(normalExchange()).with(RabbitmqConstant.MQ_WEBSITE_NORMAL_ROUTING_KEY);
    }

    // 定义消息转换器
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    /** ======================== 定制一些处理策略 =============================*/

    /**
     * 定制化amqp模版
     * <p>
     * Rabbit MQ的消息确认有两种。
     * <p>
     * 一种是消息发送确认：这种是用来确认生产者将消息发送给交换机，交换机传递给队列过程中，消息是否成功投递。
     * 发送确认分两步：一是确认是否到达交换机，二是确认是否到达队列
     * <p>
     * 第二种是消费接收确认：这种是确认消费者是否成功消费了队列中的消息。
     */
    // 定义消息模板用于发布消息，并且设置其消息转换器
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 消息发送失败返回到队列中, yml需要配置 publisher-returns: true
        rabbitTemplate.setMandatory(true);

        /**
         * 使用该功能需要开启消息确认，yml需要配置 publisher-confirms: true
         * 通过实现ConfirmCallBack接口，用于实现消息发送到交换机Exchange后接收ack回调
         * correlationData  消息唯一标志
         * ack              确认结果
         * cause            失败原因
         */
        rabbitTemplate.setConfirmCallback(new MsgSendConfirmCallback());
        /**
         * 使用该功能需要开启消息返回确认，yml需要配置 publisher-returns: true
         * 通过实现ReturnCallback接口，如果消息从交换机发送到对应队列失败时触发
         * message    消息主体 message
         * replyCode  消息主体 message
         * replyText  描述
         * exchange   消息使用的交换机
         * routingKey 消息使用的路由键
         */
        rabbitTemplate.setReturnCallback(new MsgSendReturnCallback());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("127.0.0.1:5672");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED); //必须要设置
        return connectionFactory;
    }


    // --------------------------使用RabbitAdmin启动服务便创建交换机和队列--------------------------
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // 只有设置为 true，spring 才会加载 RabbitAdmin 这个类
        rabbitAdmin.setAutoStartup(true);
        // 创建延时交换机和对列
        rabbitAdmin.declareExchange(delayExchange());
        rabbitAdmin.declareQueue(delayQueue());
        // 创建普通交换机和对列
        rabbitAdmin.declareExchange(normalExchange());
        rabbitAdmin.declareQueue(normalQueue());
        return new RabbitAdmin(connectionFactory);
    }

}

