package laboratory.astrea.redis.cache;

import laboratory.astrea.redis.operation.PubSubOperation;
import laboratory.astrea.redis.operation.ValueOperationExtension;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static io.vavr.API.*;
import static java.util.stream.Collectors.toMap;
import static laboratory.astrea.buitlin.core.Functions.runnableSupplier;
import static laboratory.astrea.buitlin.core.KCollection.listOf;
import static laboratory.astrea.buitlin.core.KCollection.mappingValue;
import static laboratory.astrea.buitlin.core.Strings.joinedString;
import static laboratory.astrea.buitlin.core.Strings.splitAsList;
import static laboratory.astrea.buitlin.core.TopLevelFunctions.nanoTime;

public final class RedisValueCache<V> extends SerializableCache<V> implements RCache<V> {

    private final String cacheName;

    private final ValueOperationExtension valueOperation;

    private final PubSubOperation pubSubOperation;

    private final RCache<V> delegateCache;

    private final Duration timeout;

    private final String channelName;

    private final UnaryOperator<String> keyTransformer = this::defaultKeyTransformer;


    private final ConcurrentMap<String, Long> updatedInfoTable = new ConcurrentHashMap<>();

    public RedisValueCache(String cacheName, ValueOperationExtension valueOperation, PubSubOperation pubSubOperation, RCache<V> delegateCache, Duration timeout, Class<V> typeReference) {
        super(typeReference);
        this.cacheName = cacheName;
        this.valueOperation = valueOperation;
        this.pubSubOperation = pubSubOperation;
        this.delegateCache = delegateCache;
        this.timeout = timeout;

        this.channelName = generateChannelName();
        postConstruct();
    }


    public RedisValueCache(String cacheName, ValueOperationExtension valueOperation, PubSubOperation pubSubOperation, RCache<V> delegateCache, Duration timeout, ParameterizedTypeReference<V> typeReference) {
        super(typeReference);
        this.cacheName = cacheName;
        this.valueOperation = valueOperation;
        this.pubSubOperation = pubSubOperation;
        this.delegateCache = delegateCache;
        this.timeout = timeout;

        this.channelName = generateChannelName();
        postConstruct();
    }

    private void postConstruct() {

        final var subscribeBy = cacheName + "@RCache";
        this.pubSubOperation.subscribeMessage(subscribeBy, channelName, this::subscribeEvent);
        this.pubSubOperation.subscribeExpiredEventMessage(subscribeBy, this::deleteEvent);
        this.pubSubOperation.subscribeDeletedEventMessage(subscribeBy, this::deleteEvent);
    }


    private String defaultKeyTransformer(String key) {
        return key + "@" + cacheName;
    }

    @Override
    public V getIfPresent(@NotNull String _key) {

        final var key = keyTransformer.apply(_key);

        var value = delegateCache.getIfPresent(key);

        if (value != null) return value;

        final var redisValue = valueOperation.get(key);

        if (redisValue == null) return null;

        return delegateCache.get(key, __ -> deserialize(redisValue));
    }

    @Override
    public V get(@NotNull String _key, @NotNull Function<String, ? extends V> mappingFunction) {

        final var key = keyTransformer.apply(_key);

        return delegateCache.get(key, k -> {
            final var redisValue = valueOperation.computeIfAbsent(key, timeout, () -> {
                final var value = serialize(mappingFunction.apply(k));
                changeUpdateInfoTableAndPublish(k);
                return value;
            });
            return deserialize(redisValue);
        });
    }

    @Override
    public void put(@NotNull String _key, @NotNull V value) {
        final var key = keyTransformer.apply(_key);
        valueOperation.set(key, serialize(value), timeout);
        changeUpdateInfoTableAndPublish(key);
        delegateCache.put(key, value);
    }


    @Override
    public void putAll(Map<String, V> _map) {
        final var map = _map.entrySet().stream()
                .collect(toMap(entry -> keyTransformer.apply(entry.getKey()), Map.Entry::getValue));
        valueOperation.setAll(mappingValue(map, this::serialize), timeout);
        changeAllUpdateInfoTableAndPublish(map.keySet());
        delegateCache.putAll(map);
    }

    @Override
    public void delete(String _key) {
        final var key = keyTransformer.apply(_key);
        valueOperation.delete(key);
        pubSubOperation.publish(channelName, serializeOther(RCacheEvent.Delete.apply(key)));
    }

    @Override
    public void deleteAll(Iterable<String> _keys) {
        final var keys = listOf(_keys).collect(keyTransformer::apply).castToList();
        valueOperation.delete(keys);
        pubSubOperation.publish(channelName, serializeOther(RCacheEvent.DeleteAll.apply(String.join(",", keys))));
    }

    @Override
    public void cleanup() {
        delegateCache.cleanup();
    }


    private void changeUpdateInfoTableAndPublish(@NotNull String key) {
        final var nanoTime = nanoTime();
        updatedInfoTable.put(key, nanoTime);
        publishUpdateEvent(key, nanoTime);
    }

    private void changeAllUpdateInfoTableAndPublish(@NotNull Iterable<String> keys) {
        final var nanoTime = nanoTime();
        keys.forEach(key -> updatedInfoTable.put(key, nanoTime));
        publishUpdateEvent(keys, nanoTime);
    }


    private void publishUpdateEvent(String key, long nanoTime) {
        pubSubOperation.publish(channelName, serializeOther(RCacheEvent.Update.apply(joinedString(key, String.valueOf(nanoTime)))));
    }

    private void publishUpdateEvent(Iterable<String> keys, long nanoTime) {
        pubSubOperation.publish(channelName, serializeOther(RCacheEvent.UpdateAll.apply(joinedString(joinedString(keys), String.valueOf(nanoTime)))));
    }

    private void deleteEvent(String key) {
        delegateCache.delete(key);
        updatedInfoTable.remove(key);
    }

    private void deleteAllEvent(Iterable<String> keys) {
        delegateCache.deleteAll(keys);
        keys.forEach(updatedInfoTable::remove);
    }

    private void updateEvent(String message) {

        final var list = splitAsList(message);

        final var key = list.get(0);
        final var updatedAt = Long.valueOf(list.get(list.size() - 1));

        eliminateCacheIfNecessary(key, updatedAt);
    }

    private void updateAllEvent(String message) {

        final var list = splitAsList(message);

        final var keys = list.subList(0, list.size() - 2);
        final var updatedAt = Long.valueOf(list.get(list.size() - 1));

        keys.forEach(key -> eliminateCacheIfNecessary(key, updatedAt));
    }

    private void eliminateCacheIfNecessary(String key, Long updatedAt) {
        final var lastUpdate = updatedInfoTable.get(key);
        if (lastUpdate == null) return;

        if (lastUpdate < updatedAt) {
            delegateCache.delete(key);
        }
    }

    private void subscribeEvent(String message) {

        final var messagePair = deserializeOther(message, R_CACHE_EVENT_MESSAGE_TYPE);

        final var eventType = messagePair.first;
        final var content = messagePair.second;

        Match(eventType).of(
                Case($(RCacheEvent.Delete), runnableSupplier(() -> deleteEvent(content))),
                Case($(RCacheEvent.DeleteAll), runnableSupplier(() -> deleteAllEvent(splitAsList(content)))),
                Case($(RCacheEvent.Update), runnableSupplier(() -> updateEvent(content))),
                Case($(RCacheEvent.UpdateAll), runnableSupplier(() -> updateAllEvent(content)))
        );
    }

    private String generateChannelName() {
        return String.format(R_CACHE_EVENT_CHANNEL_TEMPLATE, cacheName);
    }

}
