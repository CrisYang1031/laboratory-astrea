package laboratory.astrea.redis.pubsub;


import io.lettuce.core.pubsub.RedisPubSubListener;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static laboratory.astrea.buitlin.core.Functions.mappingAndTest;


@Slf4j
public final class PartialPubSubListener extends RedisPubSubListenerAdapter {

    private final String subscribeBy;
    private final Predicate<RedisSubscriptionMessage> predicate;

    PartialPubSubListener(String subscribeBy, Predicate<RedisSubscriptionMessage> predicate, Consumer<RedisSubscriptionMessage> messageConsumer) {
        super(messageConsumer);
        this.subscribeBy = subscribeBy;
        this.predicate = predicate;
    }


    public static RedisPubSubListener<String, String> ofPattern(String subscribeBy, @NotNull String subscriptionKey, Consumer<RedisSubscriptionMessage> messageConsumer) {
        return of(subscribeBy, mappingAndTest(RedisSubscriptionMessage::getPattern, subscriptionKey::equals), messageConsumer);
    }


    public static RedisPubSubListener<String, String> of(String subscribeBy, @NotNull String subscriptionKey, Consumer<RedisSubscriptionMessage> messageConsumer) {
        return of(subscribeBy, mappingAndTest(RedisSubscriptionMessage::getChannel, subscriptionKey::equals), messageConsumer);
    }


    public static RedisPubSubListener<String, String> of(String subscribeBy, @NotNull Predicate<RedisSubscriptionMessage> predicate, Consumer<RedisSubscriptionMessage> messageConsumer) {
        return new PartialPubSubListener(subscribeBy, predicate, messageConsumer);
    }

    @Override
    protected void onNext(RedisSubscriptionMessage message) {
        if (predicate.test(message)) {
            log.info("INFO#MessageSubscription - received by {} - {} - {}", subscribeBy, message, this.toString());
            messageConsumer.accept(message);
        }
    }

}
