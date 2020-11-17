package laboratory.astrea.redis.operation;


import laboratory.astrea.redis.SyncConnectionContext;

final class SynchronousQueueOperation extends SynchronousCommonOperation implements QueueOperation {


    public SynchronousQueueOperation(SyncConnectionContext connectionContext) {
        super(connectionContext);
    }


    @Override
    public boolean offer(String key, String value) {
        return redisCommands.lpush(key, value) > 0;
    }


    @Override
    public String poll(String key) {
        return redisCommands.rpop(key);
    }

    @Override
    public Integer size(String key) {
        return redisCommands.llen(key).intValue();
    }

}
