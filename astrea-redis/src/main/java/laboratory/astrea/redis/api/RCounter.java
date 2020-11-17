package laboratory.astrea.redis.api;

public interface RCounter extends RExpirable, RAny {


    Long increase();


    Long increase(long amount);

}
