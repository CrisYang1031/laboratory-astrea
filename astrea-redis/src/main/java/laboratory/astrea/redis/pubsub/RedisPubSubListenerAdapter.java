package laboratory.astrea.redis.pubsub;

import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.RedisPubSubListener;
import laboratory.astrea.redis.Specification;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public abstract class RedisPubSubListenerAdapter extends RedisPubSubAdapter<String, String> implements RedisPubSubListener<String, String> {

    protected final Consumer<RedisSubscriptionMessage> messageConsumer;

    public RedisPubSubListenerAdapter(Consumer<RedisSubscriptionMessage> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }


    protected abstract void onNext(RedisSubscriptionMessage message);

    @Override
    public void message(String channel, String message) {

        final var subscriptionMessage = RedisSubscriptionMessage.of(Specification.NONE_PATTERN, channel, message);

        onNext(subscriptionMessage);
    }

    @Override
    public void message(String pattern, String channel, String message) {

        final var subscriptionMessage = RedisSubscriptionMessage.of(pattern, channel, message);

        onNext(subscriptionMessage);
    }
}
