package com.hs.common.rabbitmq;

public class RabbitmqConstant {
    // 延时交换机
    public static final String MQ_WEBSITE_FILM_DELAY_EXCHANGE = "delay_exchange";

    // 延时队列名称
    public static final String MQ_WEBSITE_FILM_QUEUE = "delay_queue";

    // 普通交换机
    public static final String MQ_WEBSITE_NORMAL_EXCHANGE = "normal_exchange";

    // 普通队列名称
    public static final String MQ_WEBSITE_NORMAL_QUEUE = "normal_queue";

    // 普通交换机路由键
    public static final String MQ_WEBSITE_NORMAL_ROUTING_KEY = "normal_routing_key";
}
