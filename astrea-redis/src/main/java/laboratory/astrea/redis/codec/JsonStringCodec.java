package laboratory.astrea.redis.codec;

import org.springframework.core.ParameterizedTypeReference;

import static laboratory.astrea.buitlin.core.Json.jsonString;
import static laboratory.astrea.buitlin.core.Json.jsonValue;


enum JsonStringCodec implements StringCodec {

    Companion,

    ;


    @Override
    public <V> V decode(String value, Class<V> clazz) {
        if (value == null) return null;
        return jsonValue(value, clazz);
    }

    @Override
    public <V> V decode(String value, ParameterizedTypeReference<V> typeReference) {
        if (value == null) return null;
        return jsonValue(value, typeReference);
    }

    @Override
    public <V> String encode(V value) {
        return jsonString(value);
    }

}
