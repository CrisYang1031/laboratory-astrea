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

    public static @NotNull Type makeList(Class<?> elementClass) {

        return ResolvableType.forClassWithGenerics(List.class, elementClass).getType();
    }

    public static @NotNull Type makeSet(Class<?> elementClass) {

        return ResolvableType.forClassWithGenerics(Set.class, elementClass).getType();
    }

    public static @NotNull Type makeMap(Class<?> keyClass, Class<?> valueClass) {

        return ResolvableType.forClassWithGenerics(Map.class, keyClass, valueClass).getType();
    }

    public static <T> Class<T> rawTypeFromSuperGeneric(@NotNull Object instance) {
        return cast(ResolvableType.forInstance(instance).getSuperType().getGeneric(0).getRawClass());
    }

    public static <T> Class<T> rawType(ParameterizedTypeReference<T> typeReference) {
        return cast(ResolvableType.forType(typeReference.getType()).resolve());
    }
}
