package laboratory.astrea.redis.api;

import java.util.function.BiFunction;

public interface RFactory {


    <T> T proxyScopedValue(String name, Class<T> typeReference, BiFunction<String, Class<T>, RValue<T>> rValueFunction);


}
