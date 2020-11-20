package laboratory.astrea.buitlin.core;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static laboratory.astrea.buitlin.core.TopLevelFunctions.cast;


public final class Parameterized {

    private Parameterized() {
        throw new AssertionError("Suppress default constructor for non instantiated ability");
    }


    public static @NotNull <T> ParameterizedTypeReference<List<T>> synthesizeList(Class<T> elementClass) {

        final var resolvableType = ResolvableType.forClassWithGenerics(List.class, elementClass);
        return ParameterizedTypeReference.forType(resolvableType.getType());
    }

    public static @NotNull <T> ParameterizedTypeReference<Set<T>> synthesizeSet(Class<T> elementClass) {

        final var resolvableType = ResolvableType.forClassWithGenerics(Set.class, elementClass);
        return ParameterizedTypeReference.forType(resolvableType.getType());
    }

    public static @NotNull <K, V> ParameterizedTypeReference<Map<K, V>> synthesizeMap(Class<K> keyClass, Class<V> valueClass) {

        final var resolvableType = ResolvableType.forClassWithGenerics(Map.class, keyClass, valueClass);
        return ParameterizedTypeReference.forType(resolvableType.getType());
    }

    public static @NotNull <T> ParameterizedTypeReference<T> synthesizeGeneric(Class<?> rawType, ParameterizedTypeReference<?> genericType) {

        return synthesizeGeneric(rawType, genericType.getType());
    }

    public static @NotNull <T> ParameterizedTypeReference<T> synthesizeGeneric(Class<?> rawType, Type type) {

        final var t = ResolvableType.forClassWithGenerics(rawType, ResolvableType.forType(type)).getType();

        return ParameterizedTypeReference.forType(t);
    }

    public static <T> Class<T> rawTypeFromSuperGeneric(@NotNull Object instance) {
        return cast(ResolvableType.forInstance(instance).getSuperType().getGeneric(0).getRawClass());
    }


    public static <T> Class<T> rawType(ParameterizedTypeReference<T> typeReference) {
        return cast(ResolvableType.forType(typeReference.getType()).resolve());
    }

    /**
     * 获取ParameterizedTypeReference中实际类型上的泛型类型
     * <p> 例如{@code ParameterizedTypeReference<Wrapped<List<Person>>>}
     * <p> 则实际类型为Wrapped 实际际类型上的泛型类型为{@code List<Person>}
     */
    public static <T> ParameterizedTypeReference<T> actualGenericType(ParameterizedTypeReference<?> typeReference) {

        final var type = ResolvableType.forType(typeReference.getType())
                .getGeneric(0)
                .getType();

        return ParameterizedTypeReference.forType(type);
    }


}
