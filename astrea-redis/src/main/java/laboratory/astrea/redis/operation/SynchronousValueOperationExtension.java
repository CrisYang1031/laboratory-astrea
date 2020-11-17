package laboratory.astrea.redis.operation;

import io.vavr.control.Try;
import laboratory.astrea.redis.RedisLockTimeoutException;
import laboratory.astrea.redis.Specification;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.function.Supplier;

final class SynchronousValueOperationExtension implements ValueOperationExtension {

    @Delegate
    private final ValueOperation valueOperation;


    @Delegate
    private final LockOperation lockOperation;


    public SynchronousValueOperationExtension(ValueOperation valueOperation, LockOperation lockOperation) {
        this.valueOperation = valueOperation;
        this.lockOperation = lockOperation;
    }

    @Override
    public String computeIfAbsent(@NotNull String key, @NotNull Duration expire, @NotNull Supplier<String> valueSupplier) {

        final Supplier<String> computationBlock = () -> computeIfAbsentSequential(key, expire, valueSupplier);

        return Try.of(() -> lock(key, Specification.LOCK_TIMEOUT, computationBlock))
                .recover(RedisLockTimeoutException.class, throwable -> computationBlock.get())
                .get();
    }

    public String computeIfAbsentSequential(@NotNull String key, @NotNull Duration expire, @NotNull Supplier<String> valueSupplier) {

        String value = get(key);

        if (value != null) {
            return value;
        }

        value = valueSupplier.get();

        return setIfAbsent(key, expire, value) ? value : get(key);
    }


    public boolean runIfAbsent(String key, Runnable ifAbsent) {
        if (!exists(key)) {
            ifAbsent.run();
            return true;
        }
        return false;
    }
}
