package laboratory.astrea.event;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public final class RabbitEventListener {


    @RabbitListener(queues = "#{queueNamesForListener}")
    public void onEvent(Message message, Channel channel) {


    }

}
