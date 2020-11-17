package laboratory.astrea.redis.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import laboratory.astrea.buitlin.core.Pair;
import laboratory.astrea.redis.SyncConnectionContext;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.ParameterizedTypeReference;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static laboratory.astrea.buitlin.core.TopLevelFunctions.cast;


public interface RCache<V> {


    /**
     * 作为给redis订阅cacheKey事件的channelName模板
     */
    String R_CACHE_EVENT_CHANNEL_TEMPLATE = "@R^Event %s";


    /**
     * event message的类型
     */
    ParameterizedTypeReference<Pair<RCacheEvent, String>> R_CACHE_EVENT_MESSAGE_TYPE = new ParameterizedTypeReference<>() {
    };


    @Nullable V getIfPresent(@NotNull String key);


    V get(@NotNull String key, @NotNull Function<String, ? extends V> mappingFunction);


    void put(@NotNull String key, @NotNull V value);


    void putAll(Map<String, V> map);


    void delete(String key);


    void deleteAll(Iterable<String> keys);


    void cleanup();


    static <T> Builder<T> builder() {
        return new Builder<>();
    }


    @RequiredArgsConstructor
    class Builder<V> {

        private static final String DEFAULT_CAFFEINE_SPECIFICATION = "maximumSize=256, expireAfterWrite=10m";

        private String cacheName;

        private SyncConnectionContext connectionContext;

        private ParameterizedTypeReference<V> typeReference;

        private Class<V> clazz;

        private Duration timeout = Duration.ZERO;

        private String caffeineSpecification;

        private UnaryOperator<Caffeine<String, V>> caffeineCustomizer = UnaryOperator.identity();


        public Builder<V> cacheName(String cacheName) {
            this.cacheName = cacheName;
            return this;
        }

        public Builder<V> connectionContext(SyncConnectionContext connectionContext) {
            this.connectionContext = connectionContext;
            return this;
        }

        public Builder<V> type(Class<V> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder<V> type(ParameterizedTypeReference<V> typeReference) {
            this.typeReference = typeReference;
            return this;
        }

        public Builder<V> timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder<V> caffeineSpecification(String caffeineSpecification) {
            this.caffeineSpecification = caffeineSpecification;
            return this;
        }

        public Builder<V> caffeineCustomizer(UnaryOperator<Caffeine<String, V>> caffeineCustomizer) {
            this.caffeineCustomizer = caffeineCustomizer;
            return this;
        }


        public RCache<V> build() {

            if (cacheName == null) {
                throw new IllegalArgumentException("cache name can not be null");
            }

            if (connectionContext == null) {
                throw new IllegalArgumentException("connectionContext can not be null");
            }

            if (typeReference == null && clazz == null) {
                throw new IllegalArgumentException("typeReference or clazz can not both be null");
            }

            final var caffeine = Optional.ofNullable(caffeineSpecification)
                    .map((Function<String, Caffeine<Object, Object>>) Caffeine::from)
                    .orElseGet(() -> Caffeine.from(DEFAULT_CAFFEINE_SPECIFICATION));

            final var caffeineCache = caffeineCustomizer.apply(cast(caffeine)).build();

            final var vCaffeineCache = new CaffeineCache<>(caffeineCache);

            final var syncOperationFactory = connectionContext.syncOperationFactory();
            final var operationExtension = syncOperationFactory.valueExtension();
            final var pubSubOperation = syncOperationFactory.pubSubOperation();

            if (typeReference != null) {
                return new RedisValueCache<>(cacheName, operationExtension, pubSubOperation, vCaffeineCache, timeout, typeReference);
            }

            return new RedisValueCache<>(cacheName, operationExtension, pubSubOperation, vCaffeineCache, timeout, clazz);
        }

    }
}
