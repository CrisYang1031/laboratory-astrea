package laboratory.astrea.redis.cache;

import laboratory.astrea.redis.codec.StringCodec;
import org.springframework.core.ParameterizedTypeReference;

public abstract class SerializableCache<V> {

    protected final Object typeReference;

    private final StringCodec stringCodec;


    protected SerializableCache(Object typeReference, StringCodec stringCodec) {
        this.typeReference = typeReference;
        this.stringCodec = stringCodec;
    }

    protected SerializableCache(Object typeReference) {
        this(typeReference, StringCodec.json());
    }


    protected V deserialize(String value) {
        return stringCodec.decode(value, typeReference);
    }


    protected String serialize(V value) {
        return stringCodec.encode(value);
    }

    protected String serializeOther(Object value) {
        return stringCodec.encode(value);
    }

    protected <T> T deserializeOther(String value, Class<T> clazz) {
        return stringCodec.decode(value, clazz);
    }

    @SuppressWarnings("SameParameterValue")
    protected <T> T deserializeOther(String value, ParameterizedTypeReference<T> typeReference) {
        return stringCodec.decode(value, typeReference);
    }

}
