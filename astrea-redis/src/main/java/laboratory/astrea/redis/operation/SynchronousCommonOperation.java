package laboratory.astrea.redis.operation;

import io.lettuce.core.api.sync.RedisCommands;
import laboratory.astrea.redis.SyncConnectionContext;

import java.time.Duration;
import java.time.LocalDateTime;

import static laboratory.astrea.buitlin.core.KCollection.stream;


public class SynchronousCommonOperation implements IntrinsicOperation, ExpireOperation {

    protected final RedisCommands<String, String> redisCommands;

    public SynchronousCommonOperation(SyncConnectionContext connectionContext) {
        this.redisCommands = connectionContext.redisCommands();
    }

    @Override
    public boolean exists(String key) {
        return redisCommands.exists(key) == 1;
    }


    @Override
    public void delete(String key) {
        redisCommands.del(key);
    }

    @Override
    public void delete(Iterable<String> keys) {
        redisCommands.del(stream(keys).toArray(String[]::new));
    }

    @Override
    public void unlink(String key) {
        redisCommands.unlink(key);
    }

    @Override
    public void expire(String key, Duration duration) {
        redisCommands.expire(key, duration.getSeconds());
    }

    @Override
    public void expireAt(String key, LocalDateTime expireAt) {
        expire(key, Duration.between(expireAt, LocalDateTime.now()));
    }

    @Override
    public void clearExpire(String key) {
        redisCommands.persist(key);
    }

    @Override
    public Duration remainTimeToLive(String key) {
        return Duration.ofSeconds(redisCommands.ttl(key));
    }
}
