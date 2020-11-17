package laboratory.astrea.redis.operation;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.function.Supplier;

public interface ValueOperationExtension extends ValueOperation, LockOperation {


    default String computeIfAbsent(@NotNull String key, @NotNull Supplier<String> valueSupplier) {
        return computeIfAbsent(key, Duration.ZERO, valueSupplier);
    }


    String computeIfAbsent(@NotNull String key, @NotNull Duration expire, @NotNull Supplier<String> valueSupplier);


    String computeIfAbsentSequential(@NotNull String key, @NotNull Duration expire, @NotNull Supplier<String> valueSupplier);


    /**
     * @return true 不存在key， 并执行了Runnable ifAbsent
     */
    boolean runIfAbsent(String key, Runnable ifAbsent);
}
