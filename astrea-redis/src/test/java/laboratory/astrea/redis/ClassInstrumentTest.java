package laboratory.astrea.redis;

import laboratory.astrea.redis.api.RScoped;
import laboratory.astrea.redis.api.RValue;

import static laboratory.astrea.buitlin.instrument.ClassInstrumentFactory.Javassist_;

public final class ClassInstrumentTest {


    public static void main(String[] args) {

        Javassist_.create("laboratory.astrea.redis.PersonRScope$Javassist")
                .addInterfaces(RScoped.class.getName())
                .setSuperClass(Person.class.getName())
                .addField(String.format("private %s rValue;", RValue.class.getName()))
                .addMethod("public void commit() { rValue.set(this); }")
                .toClass()
        ;



    }


}
