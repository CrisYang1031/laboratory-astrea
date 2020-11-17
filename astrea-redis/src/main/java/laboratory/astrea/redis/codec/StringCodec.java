package laboratory.astrea.redis.codec;

import org.springframework.core.ParameterizedTypeReference;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;
import static laboratory.astrea.buitlin.core.TopLevelFunctions.cast;

@SuppressWarnings("unused")
public interface StringCodec {


    <V> String encode(V value);


    default <V> String encode(V value, Class<V> clazz) {
        return encode(value);
    }


    default <V> String encode(V value, ParameterizedTypeReference<V> typeReference) {
        return encode(value);
    }


    <V> V decode(String value, Class<V> clazz);


    <V> V decode(String value, ParameterizedTypeReference<V> typeReference);


    default <V> V decode(String value, Object typeReference) {
        return cast(Match(typeReference).of(
                Case($(instanceOf(Class.class)), it -> decode(value, (Class<?>) it)),
                Case($(instanceOf(ParameterizedTypeReference.class)), it -> decode(value, (ParameterizedTypeReference<?>) it))
        ));
    }



    static StringCodec json() {
        return JsonStringCodec.Companion;
    }

}
