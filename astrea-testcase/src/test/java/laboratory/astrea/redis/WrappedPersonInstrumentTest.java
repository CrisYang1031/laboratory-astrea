package laboratory.astrea.redis;

import laboratory.astrea.buitlin.instrument.ClassInstrument;
import laboratory.astrea.buitlin.instrument.InstrumentGenericType;
import laboratory.astrea.redis.api.RScoped;

import java.util.List;

import static laboratory.astrea.buitlin.instrument.ClassInstrumentFactory.Javassist_;

public final class WrappedPersonInstrumentTest {


    public static void main(String[] args) {



        Javassist_.create("laboratory.astrea.redis.PersonRScope$Javassist")
                .setInterfaces(RScoped.class.getName())
                .setSuperClass(Wrapped.class.getName())
                .setGenericType(InstrumentGenericType.of(Wrapped.class.getName()), List.of(InstrumentGenericType.nonGeneric(RScoped.class.getName())))
                .toClass()
        ;

    }
}
