package laboratory.astrea.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.resource.ClientResources;

final class SyncConnectionContextImpl implements SyncConnectionContext {

    private final RedisURI redisUri;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final RedisClient redisClient;

    private final StatefulRedisConnection<String, String> redisConnections;

    private final StatefulRedisPubSubConnection<String, String> pubSubConnections;

    public SyncConnectionContextImpl(RedisURI redisUri) {
        this(redisUri, RedisClient.create(redisUri));
    }

    public SyncConnectionContextImpl(RedisURI redisUri, ClientResources clientResources) {
        this(redisUri, RedisClient.create(clientResources, redisUri));
    }

    public SyncConnectionContextImpl(RedisURI redisUri, RedisClient redisClient) {
        this.redisUri = redisUri;
        this.redisClient = redisClient;
        this.redisConnections = redisClient.connect();
        this.pubSubConnections = redisClient.connectPubSub();
    }

    @Override
    public RedisCommands<String, String> redisCommands() {
        return redisConnections.sync();
    }

    @Override
    public StatefulRedisPubSubConnection<String, String> pubSubConnection() {
        return pubSubConnections;
    }

    @Override
    public RedisURI redisUri() {
        return redisUri;
    }

    @Override
    public RedisSyncOperationFactory syncOperationFactory() {
        return RedisSyncOperationFactory.create(this);
    }


}
