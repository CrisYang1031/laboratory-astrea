package laboratory.astrea.redis.operation;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;


public interface ValueOperation extends CommonOperation {


    String get(String key);


    default String getOrDefault(String key, String defaultValue) {
        final var value = get(key);
        return value == null ? defaultValue : value;
    }

    void set(String key, String value);


    void set(String key, String value, Duration timeout);


    void setAll(Map<String, String> map);


    void setAll(Map<String, String> map, Duration timeout);


    Long increment(String key);


    Long increment(String key, long amount);


    Long decrement(String key);


    Long decrement(String key, long amount);


    default boolean setIfAbsent(String key, String value) {
        return setIfAbsent(key, Duration.ZERO, value);
    }


    boolean setIfAbsent(String key, Duration timeout, String value);


    default boolean setIfAbsent(String key, Supplier<String> valueSupplier) {
        return setIfAbsent(key, Duration.ZERO, valueSupplier);
    }

    default boolean setIfAbsent(String key, Duration timeout, @NotNull Supplier<String> valueSupplier) {
        if (exists(key)) {
            return false;
        }
        return setIfAbsent(key, timeout, valueSupplier.get());
    }


}
