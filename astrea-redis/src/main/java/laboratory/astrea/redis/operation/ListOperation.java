package laboratory.astrea.redis.operation;

import io.vavr.collection.Array;

import java.util.List;

public interface ListOperation extends IntrinsicOperation {


    List<String> get(String key);


    String get(String key, int index);


    void append(String key, String value);


    void appendAll(String key, String... value);


    default void appendAll(String key, Iterable<String> value) {
        appendAll(key, Array.ofAll(value).toJavaArray(String[]::new));
    }

    void prepend(String key, String value);


    void prependAll(String key, String... value);


    default void prependAll(String key, Iterable<String> value) {
        prependAll(key, Array.ofAll(value).toJavaArray(String[]::new));
    }


    void set(String key, int index, String value);


    void remove(String key, int index);


    String removeLast(String key);


    String removeFirst(String key);


    int size(String key);

}
