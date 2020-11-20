package laboratory.astrea.redis.codec;

import org.springframework.core.ParameterizedTypeReference;

import static laboratory.astrea.buitlin.core.TopLevelFunctions.cast;

public enum LongStringCodec implements StringCodec {

    Companion,
    ;

    @Override
    public String encode(Object value) {

        if (value instanceof Number) {
            return value.toString();
        }

        throw new IllegalArgumentException("not acceptable type: " + value.getClass());
    }

    @Override
    public <V> V decode(String value, Class<V> clazz) {
        return cast(Long.parseLong(value));
    }

    @Override
    public <V> V decode(String value, ParameterizedTypeReference<V> typeReference) {
        return cast(Long.parseLong(value));
    }
}
