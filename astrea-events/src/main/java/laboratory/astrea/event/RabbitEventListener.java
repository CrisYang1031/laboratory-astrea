package laboratory.astrea.event;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;

@Slf4j
public final class RabbitEventListener {


    @RabbitListener(queues = "#{queueNamesForListener}")
    public void onEvent(Message message, Channel channel, @Payload Object payload) {

        log.info("received message - {}", message);
        log.info("received channel - {}", channel);
        log.info("received payload[{}] - {}", payload.getClass(), payload);
    }

}
