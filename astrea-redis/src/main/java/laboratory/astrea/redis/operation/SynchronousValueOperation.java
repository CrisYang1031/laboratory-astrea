package laboratory.astrea.redis.operation;


import laboratory.astrea.redis.Specification;
import laboratory.astrea.redis.SyncConnectionContext;

import java.time.Duration;
import java.util.Map;


final class SynchronousValueOperation extends SynchronousCommonOperation implements ValueOperation {

    public SynchronousValueOperation(SyncConnectionContext connectionContext) {
        super(connectionContext);
    }

    @Override
    public String get(String key) {
        return redisCommands.get(key);
    }

    @Override
    public void set(String key, String value) {
        redisCommands.set(key, value);
    }

    @Override
    public void set(String key, String value, Duration timeout) {
        if (timeout.isZero()) set(key, value);
        else redisCommands.set(key, value, Specification.setExArguments(timeout));
    }

    @Override
    public void setAll(Map<String, String> map) {
        redisCommands.mset(map);
    }

    @Override
    public void setAll(Map<String, String> map, Duration timeout) {
        if (timeout.isZero()) setAll(map);
        else map.forEach((k, v) -> set(k, v, timeout));
    }

    @Override
    public Long increment(String key) {
        return redisCommands.incr(key);
    }

    @Override
    public Long increment(String key, long amount) {
        return redisCommands.incrby(key, amount);
    }


    @Override
    public boolean setIfAbsent(String key, Duration timeout, String value) {
        return Specification.castResult(redisCommands.set(key, value, Specification.setNxArguments(timeout)));
    }



}
