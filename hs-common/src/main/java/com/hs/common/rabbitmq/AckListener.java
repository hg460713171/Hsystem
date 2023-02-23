package com.hs.common.rabbitmq;

import com.rabbitmq.client.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumer ACK机制：
 *  1. 设置手动签收。acknowledge="manual"
 *  2. 让监听器类实现ChannelAwareMessageListener接口
 *  3. 如果消息成功处理，则调用channel的 basicAck()签收
 *  4. 如果消息处理失败，则调用channel的basicNack()拒绝签收，broker重新发送给consumer
 *
 */

@Component
public class AckListener implements ChannelAwareMessageListener {

    private static Logger logger = LogManager.getLogger(AckListener.class);

    Map<Long,Integer> map;
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            //1.接收转换消息
            logger.info("MQ接收转换消息[{}]",new String(message.getBody()));

            //2. 处理业务逻辑
            logger.info("处理业务逻辑...");
            int i = 3/0;//出现错误
            //3. 手动签收
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("MQ拒绝签收逻辑...");
            /*
                4.拒绝签收
                第三个参数：requeue：重回队列。如果设置为true，则消息重新回到queue，broker会重新发送该消息给消费端
            */
            /*if(!map.containsKey(deliveryTag)){
                map.put(deliveryTag,1);
                channel.basicNack(deliveryTag,true,true);
            }else{
                //判断是否大于3次
                if(>3){
                     channel.basicNack(deliveryTag,false,true);
                    //记录
                }else{
                    channel.basicNack(deliveryTag,true,true);
                }

            }*/
            channel.basicNack(deliveryTag,false,false);
            //channel.basicReject(deliveryTag,true);
        }
    }
}

