package laboratory.astrea.event;

import com.rabbitmq.client.Channel;
import laboratory.astrea.buitlin.core.Json;
import org.springframework.amqp.core.Message;
import org.springframework.core.ParameterizedTypeReference;

import static laboratory.astrea.buitlin.core.Functions.Try;
import static laboratory.astrea.buitlin.core.Json.jsonValue;

public final class RabbitMessageCarrier implements MessageCarrier {

    private final Message message;

    private final Channel channel;

    private Object payload;

    public RabbitMessageCarrier(Message message, Channel channel, Object payload) {
        this.message = message;
        this.channel = channel;
    }


    @Override
    public Message message() {
        return message;
    }

    @Override
    public <T> T payload(Class<T> clazz) {
        //noinspection unchecked
        return (T) (payload == null ? payload = jsonValue(message.getBody(), clazz)  : payload);
    }

    @Override
    public <T> T payload(ParameterizedTypeReference<T> typeReference) {
        return null;
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
