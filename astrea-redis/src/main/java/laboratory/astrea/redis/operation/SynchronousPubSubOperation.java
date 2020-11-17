package laboratory.astrea.redis.operation;

import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import io.vavr.collection.CharSeq;
import laboratory.astrea.redis.SyncConnectionContext;
import laboratory.astrea.redis.pubsub.PartialPubSubListener;
import laboratory.astrea.redis.pubsub.RedisSubscriptionMessage;

import java.text.MessageFormat;
import java.util.function.Consumer;
import java.util.function.Predicate;

final class SynchronousPubSubOperation implements PubSubOperation {

    private final SyncConnectionContext connectionContext;

    private final StatefulRedisPubSubConnection<String, String> pubSubConnection;

    private final RedisPubSubCommands<String, String> pubSubCommands;

    private final String keyEventExpiredPattern;

    private final String keyEventDeletedPattern;

    public SynchronousPubSubOperation(SyncConnectionContext connectionContext) {

        this.connectionContext = connectionContext;
        this.pubSubConnection = connectionContext.pubSubConnection();
        this.pubSubCommands = pubSubConnection.sync();

        final var databaseIndex = connectionContext.redisUri().getDatabase();

        this.keyEventExpiredPattern = MessageFormat.format(KEY_EVENT_EXPIRED_PATTERN, databaseIndex) ;
        this.keyEventDeletedPattern = MessageFormat.format(KEY_EVENT_DELETED_PATTERN, databaseIndex) ;

        postConstruct();
    }

    private void postConstruct() {

        final var redisCommands = connectionContext.redisCommands();

        final var eventConfiguration = redisCommands.configGet(NOTIFY_KEYSPACE_EVENTS).getOrDefault(NOTIFY_KEYSPACE_EVENTS, "");

        final var eventFeatureConfiguration = CharSeq.of(eventConfiguration.toCharArray());

        final var additional = eventFeatureConfiguration.appendAll(EVENT_EXPIRED_FEATURES).distinct().mkString();

        redisCommands.configSet(NOTIFY_KEYSPACE_EVENTS, additional);
    }


    @Override
    public void subscribe(String subscribeBy, String subscriptionKey, Consumer<RedisSubscriptionMessage> messageConsumer) {
        pubSubConnection.addListener(PartialPubSubListener.of(subscribeBy, subscriptionKey, messageConsumer));
        pubSubCommands.subscribe(subscriptionKey);
    }

    @Override
    public void patternSubscribe(String subscribeBy, String subscriptionPattern, Predicate<RedisSubscriptionMessage> messagePredicate, Consumer<RedisSubscriptionMessage> messageConsumer) {
        pubSubConnection.addListener(PartialPubSubListener.of(subscribeBy, messagePredicate, messageConsumer));
        pubSubCommands.psubscribe(subscriptionPattern);
    }

    @Override
    public void patternSubscribe(String subscribeBy, String subscriptionPattern, Consumer<RedisSubscriptionMessage> messageConsumer) {
        pubSubConnection.addListener(PartialPubSubListener.ofPattern(subscribeBy, subscriptionPattern, messageConsumer));
        pubSubCommands.psubscribe(subscriptionPattern);
    }


    @Override
    public void publish(String channel, String message) {
        connectionContext.redisCommands().publish(channel, message);
    }

    @Override
    public String getKeyEventExpiredPattern() {
        return keyEventExpiredPattern;
    }

    @Override
    public String getKeyEventDeletedPattern() {
        return keyEventDeletedPattern;
    }
}
