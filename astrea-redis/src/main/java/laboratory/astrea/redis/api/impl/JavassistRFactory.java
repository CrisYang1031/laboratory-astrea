package laboratory.astrea.redis.api.impl;

import laboratory.astrea.redis.api.RAny;
import laboratory.astrea.redis.api.RFactory;
import laboratory.astrea.redis.api.RScoped;
import laboratory.astrea.redis.api.RValue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import static laboratory.astrea.buitlin.core.TopLevelFunctions.cast;
import static laboratory.astrea.buitlin.instrument.ClassInstrumentFactory.Javassist_;

final class JavassistRFactory implements RFactory {

    private final Function<Class<?>, String> nameFunction = clazz -> String.format("%sRScope$Instrument_by_Javassist", clazz.getName());

    private final Map<Class<?>, Class<?>> classLookup = new ConcurrentHashMap<>();

    @Override
    public <T> T proxyScopedValue(String name, Class<T> type, BiFunction<String, Class<T>, RValue<T>> rValueFunction) {

        final var clazz = classLookup.computeIfAbsent(type, __ -> Javassist_.create(nameFunction.apply(type))
                .addInterfaces(RScoped.class.getName())
                .setSuperClass(type.getName())
                .addField(String.format("private %s rValue;", RValue.class.getName()))
                .addMethod("public void commit() { rValue.set(this); }")
                .addMethod(String.format("public void associate(%s rObject) { this.rValue = (%s) rObject; }", RAny.class.getName(), RValue.class.getName()))
                .toClass());

        final var rValue = rValueFunction.apply(name, cast(clazz));
        final var instance = rValue.get();
        ((RScoped) instance).associate(rValue);
        return instance;
    }

}
