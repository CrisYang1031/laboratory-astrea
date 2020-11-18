package laboratory.astrea.redis.api;

public interface RCounter extends RExpirable, RAny {


    Long get();


    Long increase();


    Long increase(long amount);


    Long decrease();


    Long decrease(long amount);
}
