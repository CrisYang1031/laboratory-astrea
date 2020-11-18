package laboratory.astrea.redis.api.impl;

import io.vavr.Predicates;
import laboratory.astrea.buitlin.core.Json;
import laboratory.astrea.buitlin.core.Parameterized;
import laboratory.astrea.redis.api.RValue;
import laboratory.astrea.redis.codec.StringCodec;
import laboratory.astrea.redis.operation.CommonOperation;
import laboratory.astrea.redis.operation.ValueOperationExtension;
import org.springframework.core.ParameterizedTypeReference;

import java.util.function.Supplier;

import static laboratory.astrea.buitlin.core.TopLevelFunctions.cast;

final class RValueImpl<T> extends RCommonImpl implements RValue<T> {

    private final Object typeReference;
    private final ValueOperationExtension operation;
    private final StringCodec stringCodec;
    private final String defaultValue;

    protected RValueImpl(String name, Object typeReference, ValueOperationExtension valueOperationExtension, StringCodec stringCodec) {
        super(name);
        this.typeReference = typeReference;
        this.operation = valueOperationExtension;
        this.stringCodec = stringCodec;
        this.defaultValue = assignDefaultValue();
        typeSafe(typeReference);
    }

    private String assignDefaultValue() {
        Class<?> clazz;

        if (typeReference.getClass() == Class.class) {
            clazz = cast(typeReference);
        } else {
            clazz = Parameterized.rawType(cast(typeReference));
        }

        return clazz.isArray() || Iterable.class.isAssignableFrom(clazz) ? Json.EMPTY_ARRAY : Json.EMPTY_OBJECT;
    }


    private void typeSafe(Object typeReference) {
        if (Predicates.anyOf(Class.class::isInstance, ParameterizedTypeReference.class::isInstance).negate().test(typeReference)) {
            throw new IllegalArgumentException("typeReference in RValueImpl must be a instance of Class or ParameterizedTypeReference");
        }
    }

    @Override
    public T get() {
        return deserialize(operation.getOrDefault(name, defaultValue));
    }

    @Override
    public T getIfPresent() {
        return deserialize(operation.get(name));
    }

    @Override
    public void set(T value) {
        operation.set(name, serialize(value));
    }

    @Override
    public boolean setIfAbsent(String key, T value) {
        return operation.setIfAbsent(name, serialize(value));
    }

    @Override
    public boolean setIfAbsent(String key, Supplier<T> valueSupplier) {
        return operation.setIfAbsent(key, () -> serialize(valueSupplier.get()));
    }

    @Override
    protected CommonOperation commonOperation() {
        return operation;
    }


    private T deserialize(String value) {
        return stringCodec.decode(value, typeReference);
    }


    private String serialize(T value) {
        return stringCodec.encode(value);
    }

}
