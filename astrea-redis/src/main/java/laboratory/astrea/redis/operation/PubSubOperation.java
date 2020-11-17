package laboratory.astrea.redis.operation;

import io.vavr.collection.CharSeq;
import laboratory.astrea.redis.Specification;
import laboratory.astrea.redis.pubsub.RedisSubscriptionMessage;

import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public interface PubSubOperation {

    String KEY_EVENT_EXPIRED_PATTERN = "__keyevent@{0}__:expired";

    String KEY_EVENT_DELETED_PATTERN = "__keyevent@{0}__:del";

    String NOTIFY_KEYSPACE_EVENTS = "notify-keyspace-events";

    CharSeq EVENT_EXPIRED_FEATURES = CharSeq.of('x', 'E', 'g');


    void subscribe(String subscribeBy, String subscriptionKey, Consumer<RedisSubscriptionMessage> messageConsumer);

    default void subscribe(String subscriptionKey, Consumer<RedisSubscriptionMessage> messageConsumer) {
        subscribe(Specification.ANONYMOUS_SUBSCRIBER, subscriptionKey, messageConsumer);
    }

    default void subscribeMessage(String subscriptionKey, Consumer<String> messageConsumer) {
        subscribe(subscriptionKey, subscriptionMessage -> messageConsumer.accept(subscriptionMessage.getMessage()));
    }

    default void subscribeMessage(String subscribeBy, String subscriptionKey, Consumer<String> messageConsumer) {
        subscribe(subscribeBy, subscriptionKey, subscriptionMessage -> messageConsumer.accept(subscriptionMessage.getMessage()));
    }

    default void subscribesMessage(Iterable<String> subscriptionKeys, Consumer<String> messageConsumer) {
        subscriptionKeys.forEach(it -> subscribeMessage(it, messageConsumer));
    }

    void patternSubscribe(String subscribeBy, String subscriptionPattern, Predicate<RedisSubscriptionMessage> messagePredicate, Consumer<RedisSubscriptionMessage> messageConsumer);


    default void patternSubscribe(String subscriptionPattern, Predicate<RedisSubscriptionMessage> messagePredicate, Consumer<RedisSubscriptionMessage> messageConsumer) {
        patternSubscribe(Specification.ANONYMOUS_SUBSCRIBER, subscriptionPattern, messagePredicate, messageConsumer);
    }

    void patternSubscribe(String subscribeBy, String subscriptionPattern, Consumer<RedisSubscriptionMessage> messageConsumer);


    default void patternSubscribe(String subscriptionPattern, Consumer<RedisSubscriptionMessage> messageConsumer) {
        patternSubscribe(Specification.ANONYMOUS_SUBSCRIBER, subscriptionPattern, messageConsumer);
    }

    default void patternSubscribeMessage(String subscriptionPattern, Consumer<String> messageConsumer) {
        patternSubscribe(subscriptionPattern, subscriptionMessage -> messageConsumer.accept(subscriptionMessage.getMessage()));
    }

    default void patternSubscribeMessage(String subscribeBy, String subscriptionPattern, Consumer<String> messageConsumer) {
        patternSubscribe(subscribeBy, subscriptionPattern, subscriptionMessage -> messageConsumer.accept(subscriptionMessage.getMessage()));
    }

    default void subscribeExpiredEventMessage(Consumer<String> messageConsumer) {
        subscribeExpiredEventMessage(Specification.ANONYMOUS_SUBSCRIBER, messageConsumer);
    }

    default void subscribeExpiredEventMessage(String subscribeBy, Consumer<String> messageConsumer) {
        subscribeExpiredEventMessage(subscribeBy, message -> !message.endsWith(LockOperation.LOCKING_KEY_SUFFIX), messageConsumer);
    }

    default void subscribeExpiredEventMessage(Predicate<String> messagePredicate, Consumer<String> messageConsumer) {
        subscribeExpiredEventMessage(Specification.ANONYMOUS_SUBSCRIBER, messagePredicate, messageConsumer);
    }

    default void subscribeExpiredEventMessage(String subscribeBy, Predicate<String> messagePredicate, Consumer<String> messageConsumer) {
        patternSubscribe(subscribeBy, getKeyEventExpiredPattern()
                , subscriptionMessage -> {
                    final var channel = subscriptionMessage.getChannel();
                    final var message = subscriptionMessage.getMessage();
                    return getKeyEventExpiredPattern().equals(channel) && messagePredicate.test(message);
                }
                , subscriptionMessage -> messageConsumer.accept(subscriptionMessage.getMessage()));
    }

    default void subscribeDeletedEventMessage(Consumer<String> messageConsumer) {
        subscribeDeletedEventMessage(Specification.ANONYMOUS_SUBSCRIBER, messageConsumer);
    }

    default void subscribeDeletedEventMessage(String subscribeBy, Consumer<String> messageConsumer) {
        subscribeDeletedEventMessage(subscribeBy, message -> !message.endsWith(LockOperation.LOCKING_KEY_SUFFIX), messageConsumer);
    }

    default void subscribeDeletedEventMessage(Predicate<String> messagePredicate, Consumer<String> messageConsumer) {
        subscribeDeletedEventMessage(Specification.ANONYMOUS_SUBSCRIBER, messagePredicate, messageConsumer);
    }

    default void subscribeDeletedEventMessage(String subscribeBy, Predicate<String> messagePredicate, Consumer<String> messageConsumer) {
        patternSubscribe(subscribeBy, getKeyEventDeletedPattern()
                , subscriptionMessage -> {
                    final var channel = subscriptionMessage.getChannel();
                    final var message = subscriptionMessage.getMessage();
                    return getKeyEventDeletedPattern().equals(channel) && messagePredicate.test(message);
                }
                , subscriptionMessage -> messageConsumer.accept(subscriptionMessage.getMessage()));
    }


    void publish(String channel, String message);



    String getKeyEventExpiredPattern();



    String getKeyEventDeletedPattern();
}
