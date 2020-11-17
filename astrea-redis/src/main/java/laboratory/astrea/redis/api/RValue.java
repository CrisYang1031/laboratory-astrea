package laboratory.astrea.redis.api;

import java.util.function.Supplier;

public interface RValue<T> extends RExpirable, RAny {


    T get();



    void set(T value);



    boolean setIfAbsent(String key, T value);



    boolean setIfAbsent(String key, Supplier<T> valueSupplier);

}
