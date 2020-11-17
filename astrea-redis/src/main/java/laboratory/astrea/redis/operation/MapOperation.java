package laboratory.astrea.redis.operation;

public interface MapOperation extends CommonOperation {


    String get(String name, String key);


    void put(String name, String key, String value);


    void remove(String name, String key);


    boolean containsKey(String name, String key);


    int size(String name);


    default boolean isEmpty(String name) {
        return size(name) == 0;
    }

    void clear(String name);


    String putIfAbsent(String name, String key, String value);


    Iterable<String> values();
}
