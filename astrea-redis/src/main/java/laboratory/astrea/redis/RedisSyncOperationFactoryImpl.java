package laboratory.astrea.redis;

import laboratory.astrea.redis.operation.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class RedisSyncOperationFactoryImpl implements RedisSyncOperationFactory {

    private final SyncConnectionContext connectionContext;

    @Override
    public ValueOperation valueOperation() {
        return SynchronousOperations.valueOperation(connectionContext);
    }

    @Override
    public LockOperation lockOperation() {
        return SynchronousOperations.lockOperation(connectionContext);
    }

    @Override
    public PubSubOperation pubSubOperation() {
        return SynchronousOperations.subscribeOperation(connectionContext);
    }

    @Override
    public ValueOperationExtension valueExtension() {
        return SynchronousOperations.valueOperationExtension(connectionContext);
    }
}
