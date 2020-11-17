package laboratory.astrea.redis;


import laboratory.astrea.buitlin.core.ExpectedException;

/**
 *
 */
public final class RedisLockInterruptedException extends ExpectedException {

    public RedisLockInterruptedException(String message) {
        super(message);
    }

}
