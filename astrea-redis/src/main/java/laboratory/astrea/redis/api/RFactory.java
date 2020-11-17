package laboratory.astrea.redis.api;

public interface RFactory {


    <T> T proxyScopedValue(String name , Class<T> typeReference, RValue<T> rValue);


}
