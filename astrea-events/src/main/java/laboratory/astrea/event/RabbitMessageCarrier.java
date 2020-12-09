package laboratory.astrea.event;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

import static laboratory.astrea.buitlin.core.Functions.Try;

public final class RabbitMessageCarrier implements MessageCarrier {

    private final Message message;

    private final Channel channel;

    private final Object payload;


    public RabbitMessageCarrier(Message message, Channel channel, Object payload) {
        this.message = message;
        this.channel = channel;
        this.payload = payload;
    }


    @Override
    public Message message() {
        return message;
    }

    @Override
    public <T> T payload() {
        //noinspection unchecked
        return (T) payload;
    }

    @Override
    public void ack() {

        final var deliveryTag = message.getMessageProperties().getDeliveryTag();

        Try(() -> channel.basicAck(deliveryTag, false));
    }

    @Override
    public void reject() {

        final var deliveryTag = message.getMessageProperties().getDeliveryTag();

        Try(() -> channel.basicReject(deliveryTag, false));

    }
}
