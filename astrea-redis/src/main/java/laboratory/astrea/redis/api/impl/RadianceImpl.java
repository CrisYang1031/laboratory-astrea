package laboratory.astrea.redis.api.impl;

import io.vavr.collection.List;
import laboratory.astrea.redis.SyncConnectionContext;
import laboratory.astrea.redis.api.*;
import laboratory.astrea.redis.codec.StringCodec;
import laboratory.astrea.redis.operation.ValueOperationExtension;
import org.springframework.core.ParameterizedTypeReference;

import java.util.HashMap;
import java.util.Map;

public final class RadianceImpl implements Radiance {

    private final ValueOperationExtension valueExtension;

    private final RFactory rFactory = new RFactoryImpl();

    private final Map<Thread, List<RScoped>> scopeAggregation = new HashMap<>();

    public RadianceImpl(SyncConnectionContext syncConnectionContext) {
        final var redisSyncOperationFactory = syncConnectionContext.syncOperationFactory();
        this.valueExtension = redisSyncOperationFactory.valueExtension();

    }

    @Override
    public <T> RValue<T> getValue(String name, Class<T> typeReference) {
        return new RValueImpl<>(name, typeReference, valueExtension, StringCodec.json());
    }

    @Override
    public <T> RValue<T> getValue(String name, ParameterizedTypeReference<T> typeReference) {
        return new RValueImpl<>(name, typeReference, valueExtension, StringCodec.json());
    }

    @Override
    public void radianceScope(Runnable runnable) {

        final var currentThread = Thread.currentThread();

        scopeAggregation.put(currentThread, List.empty());

        try {
            runnable.run();

            scopeAggregation.get(currentThread).forEach(RScoped::commit);

        } finally {

            scopeAggregation.remove(currentThread);
        }
    }

    @Override
    public <T> T scopedValue(String name, Class<T> typeReference) {

        final var scopedValue = rFactory.proxyScopedValue(name, typeReference, getValue(name, typeReference));

        final var currentThread = Thread.currentThread();

        final var scopeList = scopeAggregation.get(currentThread);

        if (scopeList == null) {
            throw new IllegalStateException("can not get a scoped value in without redis scope");
        }

        scopeAggregation.put(currentThread, scopeList.prepend((RScoped) scopedValue));

        return scopedValue;
    }

    @Override
    public <T> T scopedValue(String name, ParameterizedTypeReference<T> typeReference) {
        return null;
    }
}
