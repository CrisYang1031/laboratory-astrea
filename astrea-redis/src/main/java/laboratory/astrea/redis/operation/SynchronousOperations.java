package laboratory.astrea.redis.operation;


import laboratory.astrea.buitlin.core.Pair;
import laboratory.astrea.redis.SyncConnectionContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static laboratory.astrea.buitlin.core.Functions.mappingAndApply;


public final class SynchronousOperations {

    private static final ConcurrentMap<Pair<SyncConnectionContext, Class<?>>, Object> CACHED = new ConcurrentHashMap<>();

    private SynchronousOperations() {
        throw new AssertionError("Suppress default constructor for non instantiated ability");
    }

    public static ValueOperation valueOperation(SyncConnectionContext connectionContext) {
        return (ValueOperation) CACHED.computeIfAbsent(Pair.of(connectionContext, SynchronousValueOperation.class), mappingAndApply(Pair::first, SynchronousValueOperation::new));
    }

    public static ValueOperationExtension valueOperationExtension(SyncConnectionContext connectionContext) {
        return new SynchronousValueOperationExtension(valueOperation(connectionContext), lockOperation(connectionContext));
    }


    public static LockOperation lockOperation(SyncConnectionContext connectionContext) {
        return (LockOperation) CACHED.computeIfAbsent(Pair.of(connectionContext, SynchronousLockOperation.class), mappingAndApply(Pair::first, SynchronousLockOperation::new));
    }

    public static PubSubOperation subscribeOperation(SyncConnectionContext connectionContext) {
        return (PubSubOperation) CACHED.computeIfAbsent(Pair.of(connectionContext, SynchronousPubSubOperation.class), mappingAndApply(Pair::first, SynchronousPubSubOperation::new));
    }
}
