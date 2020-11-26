package laboratory.astrea.redis.operation;

import laboratory.astrea.redis.RedisLockInterruptedException;
import laboratory.astrea.redis.RedisLockTimeoutException;
import laboratory.astrea.redis.SyncConnectionContext;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Comparator;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static laboratory.astrea.buitlin.core.TopLevelFunctions.sneakThrow;


@Slf4j
final class SynchronousLockOperation implements LockOperation {

    private static final String LOCKING_CHANNEL = "^locking@RChannel";

    private static final Semaphore IGNORED_SEMAPHORE = new Semaphore(0);

    private final ValueOperation valueOperation;

    private final PubSubOperation pubSubOperation;


    private final ConcurrentMap<String, Semaphore> semaphoreMap = new ConcurrentSkipListMap<>(Comparator.naturalOrder());


    public SynchronousLockOperation(SyncConnectionContext connectionContext) {

        this.valueOperation = new SynchronousValueOperation(connectionContext);
        this.pubSubOperation = new SynchronousPubSubOperation(connectionContext);

        postConstruct();
    }

    private void postConstruct() {

        this.pubSubOperation.subscribeMessage(LOCKING_CHANNEL, this::handleLockingKey);

        this.pubSubOperation.subscribeExpiredEventMessage("@LockOperation", LockOperation::isLockingKey, this::handleLockingKey);
    }


    @Override
    public <T> T lock(String key, Duration timeout, Supplier<T> supplierBlock) {

        final var name = LockOperation.getOccupyName();
        final var lockingKey = key + LOCKING_KEY_SUFFIX;

        try {
            //如果获取锁失败
            while (!valueOperation.setIfAbsent(lockingKey, timeout, name)) {
                final var semaphore = semaphoreMap.computeIfAbsent(lockingKey, s -> new Semaphore(0));
                try {
                    final var acquired = semaphore.tryAcquire(timeout.toMillis(), TimeUnit.MILLISECONDS);
                    if (!acquired) {
                        throw new RedisLockTimeoutException(String.format("waiting for lock %s timeout in %dms", key, timeout.toMillis()));
                    }
                } catch (InterruptedException e) {
                    if (Thread.interrupted()) {
                        throw new RedisLockInterruptedException("redis locking operation interrupted");
                    }
                    sneakThrow(e);
                }
            }
            //到这里代表已经成功获取到了锁，执行代码逻辑
            return supplierBlock.get();

        } finally {
            //最后无论成功与否，如果当前调用者持有该锁，则将锁删除
            if (name.equals(valueOperation.get(lockingKey))) {
                valueOperation.delete(lockingKey);
                pubSubOperation.publish(LOCKING_CHANNEL, lockingKey);
            }
        }
    }


    private void handleLockingKey(String key) {

        final var semaphore = semaphoreMap.getOrDefault(key, IGNORED_SEMAPHORE);

        if (semaphore.hasQueuedThreads()) {
            semaphore.release();
        } else {
            semaphoreMap.remove(key);
            log.debug("has not queued threads");
        }
    }

}
