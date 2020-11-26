package laboratory.astrea.redis.api.impl;

import laboratory.astrea.buitlin.instrument.InstrumentGenericType;
import laboratory.astrea.buitlin.metadata.MetadataScanner;
import laboratory.astrea.redis.api.RAny;
import laboratory.astrea.redis.api.RProxyFactory;
import laboratory.astrea.redis.api.RScoped;
import laboratory.astrea.redis.api.RValue;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import static laboratory.astrea.buitlin.core.Parameterized.*;
import static laboratory.astrea.buitlin.core.TopLevelFunctions.cast;
import static laboratory.astrea.buitlin.instrument.InstrumentFactory.Javassist_;

final class JavassistRProxyFactory implements RProxyFactory {

    private final Function<Class<?>, String> nameFunction = clazz -> String.format("%sRScope$Instrument_by_Javassist", clazz.getName());

    private final Map<Class<?>, Class<?>> classLookup = new ConcurrentHashMap<>();


    @Override
    public <T> T proxyScopedValue(String name, Class<T> type, BiFunction<String, Class<T>, RValue<T>> rValueFunction) {

        final var clazz = classLookup.computeIfAbsent(type, __ -> {

            final var annotationMetadata = MetadataScanner.forClassName(type.getName());

            if (annotationMetadata.isFinal() || !annotationMetadata.isConcrete()) {
                throw new IllegalArgumentException("only non-final, concrete type can be accepted by RProxyFactory - source class is: " + type);
            }

            return Javassist_.create(nameFunction.apply(type))
                    .setInterfaces(RScoped.class.getName())
                    .setSuperClass(type.getName())
                    .addField(String.format("private %s rValue;", RValue.class.getName()))
                    .addMethod("public void commit() { rValue.set(this); }")
                    .addMethod(String.format("public void associate(%s rObject) { this.rValue = (%s) rObject; }", RAny.class.getName(), RValue.class.getName()))
                    .toClass();
        });

        final var rValue = rValueFunction.apply(name, cast(clazz));
        final var instance = rValue.get();
        ((RScoped) instance).associate(rValue);
        return instance;
    }

    @Override
    public <T> T proxyScopedValue(String name, ParameterizedTypeReference<T> typeReference, BiFunction<String, ParameterizedTypeReference<T>, RValue<T>> rValueFunction) {

        final var type = rawType(typeReference);

        final var clazz = classLookup.computeIfAbsent(type, __ -> {

            final var annotationMetadata = MetadataScanner.forClassName(type.getName());

            if (annotationMetadata.isFinal() || !annotationMetadata.isConcrete()) {
                throw new IllegalArgumentException("only non-final, concrete type can be accepted by RProxyFactory - source class is: " + type);
            }

            return Javassist_.create(nameFunction.apply(type))
                    .setInterfaces(RScoped.class.getName())
                    .setSuperClass(type.getName())
                    .setGenericType(InstrumentGenericType.of(type.getName()), List.of(InstrumentGenericType.nonGeneric(RScoped.class.getName())))
                    .addField(String.format("private %s rValue;", RValue.class.getName()))
                    .addMethod("public void commit() { rValue.set(this); }")
                    .addMethod(String.format("public void associate(%s rObject) { this.rValue = (%s) rObject; }", RAny.class.getName(), RValue.class.getName()))
                    .toClass();
        });

        final var actualGenericType = actualGenericType(typeReference);

        final var synthesizeTypeReference = synthesizeGeneric(clazz, actualGenericType);

        final var rValue = rValueFunction.apply(name, cast(synthesizeTypeReference));

        final var instance = rValue.get();
        ((RScoped) instance).associate(rValue);
        return instance;
    }

}
