package laboratory.astrea.redis;

import io.lettuce.core.RedisURI;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.resource.ClientResources;

public interface SyncConnectionContext {


    RedisCommands<String, String> redisCommands();


    StatefulRedisPubSubConnection<String, String> pubSubConnection();


    RedisURI redisUri();


    RedisSyncOperationFactory syncOperationFactory();


    //----------------------   static   ----------------------

    static SyncConnectionContext create() {
        return new SyncConnectionContextImpl(RedisURI.create("redis://localhost"));
    }

    static SyncConnectionContext create(RedisURI redisUri) {
        return new SyncConnectionContextImpl(redisUri);
    }

    static SyncConnectionContext create(RedisURI redisUri, ClientResources clientResources) {
        return new SyncConnectionContextImpl(redisUri, clientResources);
    }


}
