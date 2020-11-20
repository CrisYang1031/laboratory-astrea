package laboratory.astrea.redis.api;

import org.springframework.core.ParameterizedTypeReference;

import java.util.function.BiFunction;

public interface RProxyFactory {


    <T> T proxyScopedValue(String name, Class<T> typeReference, BiFunction<String, Class<T>, RValue<T>> rValueFunction);


    <T> T proxyScopedValue(String name, ParameterizedTypeReference<T> typeReference, BiFunction<String, ParameterizedTypeReference<T>, RValue<T>> rValueFunction);

}
