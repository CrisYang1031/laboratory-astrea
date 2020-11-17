package laboratory.astrea.redis.operation;


import laboratory.astrea.redis.SyncConnectionContext;

import java.util.List;

final class SynchronousListOperation extends SynchronousCommonOperation implements ListOperation {

    private static final String DELETE_BY_RADIANCE = "^delete_by_radiance";

    public SynchronousListOperation(SyncConnectionContext connectionContext) {
        super(connectionContext);
    }

    @Override
    public List<String> get(String key) {
        return redisCommands.lrange(key, 0, -1);
    }

    @Override
    public String get(String key, int index) {
        return redisCommands.lindex(key, index);
    }

    @Override
    public void append(String key, String value) {
        redisCommands.rpush(key, value);
    }

    @Override
    public void appendAll(String key, String... value) {
        redisCommands.rpush(key, value);
    }

    @Override
    public void prepend(String key, String value) {
        redisCommands.lpush(key, value);
    }

    @Override
    public void prependAll(String key, String... value) {
        redisCommands.lpush(key, value);
    }

    @Override
    public void set(String key, int index, String value) {
        redisCommands.lset(key, index, value);
    }

    @Override
    public void remove(String key, int index) {
        set(key, index, DELETE_BY_RADIANCE);
        redisCommands.lrem(key, 1, DELETE_BY_RADIANCE);
    }

    @Override
    public String removeLast(String key) {
        return redisCommands.rpop(key);
    }

    @Override
    public String removeFirst(String key) {
        return redisCommands.lpop(key);
    }

    @Override
    public int size(String key) {
        return redisCommands.llen(key).intValue();
    }

}
