package laboratory.astrea.redis.operation;

public interface QueueOperation extends IntrinsicOperation {



    boolean offer(String key, String value);



    String poll(String key);



    Integer size(String key);
}
