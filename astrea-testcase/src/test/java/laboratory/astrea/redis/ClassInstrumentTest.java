package laboratory.astrea.redis;

import laboratory.astrea.redis.api.RAny;
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
                .addMethod(String.format("public void associate(%s rObject) { this.rValue = (%s) rObject; }", RAny.class.getName(), RValue.class.getName()))
                .toClass()
        ;


    }


}
