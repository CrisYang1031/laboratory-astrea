package laboratory.astrea.redis.api;

import java.util.function.Supplier;

public interface RValue<T> extends RExpirable, RAny {


    T get();



    T getIfPresent();



    void set(T value);



    boolean setIfAbsent(String key, T value);



    boolean setIfAbsent(String key, Supplier<T> valueSupplier);

}
