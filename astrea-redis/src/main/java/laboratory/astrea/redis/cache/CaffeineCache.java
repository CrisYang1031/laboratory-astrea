package laboratory.astrea.redis.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

final class CaffeineCache<V> implements RCache<V> {

    private final Cache<String, V> cache;

    public CaffeineCache(Cache<String, V> cache) {
        this.cache = cache;
    }

    @Override
    public @Nullable V getIfPresent(@NotNull String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public V get(@NotNull String key, @NotNull Function<String, ? extends V> mappingFunction) {
        return cache.get(key, mappingFunction);
    }

    @Override
    public void put(@NotNull String key, @NotNull V value) {
        cache.put(key, value);
    }

    @Override
    public void putAll(Map<String, V> map) {
        cache.putAll(map);
    }

    @Override
    public void delete(String key) {
        cache.invalidate(key);
    }

    @Override
    public void deleteAll(Iterable<String> keys) {
        cache.invalidateAll(keys);
    }

    @Override
    public void cleanup() {
        cache.cleanUp();
    }
}
