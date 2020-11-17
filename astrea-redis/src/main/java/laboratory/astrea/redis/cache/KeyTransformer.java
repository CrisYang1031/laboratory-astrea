package laboratory.astrea.redis.cache;

import java.util.function.Function;

@FunctionalInterface
public interface KeyTransformer<T> extends Function<T, String> {
}
