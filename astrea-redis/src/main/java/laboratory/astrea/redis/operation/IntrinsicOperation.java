package laboratory.astrea.redis.operation;

public interface IntrinsicOperation {


    boolean exists(String key);


    void delete(String key);


    void delete(Iterable<String> keys);


    void unlink(String key);

}
