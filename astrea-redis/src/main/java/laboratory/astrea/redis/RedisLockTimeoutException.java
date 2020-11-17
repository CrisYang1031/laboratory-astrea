package laboratory.astrea.redis;


import laboratory.astrea.buitlin.core.ExpectedException;

/**
 * 这里RedisLockTimeoutException不填充StackTrace,建议调用方需捕获并处理该异常
 */
public final class RedisLockTimeoutException extends ExpectedException {

    public RedisLockTimeoutException(String message) {
        super("RedisLockTimeout", message);
    }

}
