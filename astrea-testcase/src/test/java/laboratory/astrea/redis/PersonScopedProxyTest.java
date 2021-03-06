package laboratory.astrea.redis;

import laboratory.astrea.test.model.Person;
import laboratory.astrea.redis.api.RAny;
import laboratory.astrea.redis.api.RScoped;
import laboratory.astrea.redis.api.RValue;
import laboratory.astrea.redis.api.Radiance;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

import static laboratory.astrea.buitlin.instrument.InstrumentFactory.Javassist_;

public final class PersonScopedProxyTest {

    public static void main(String[] args) {

        final Class<?> clazz = Javassist_.create("laboratory.astrea.redis.PersonRScope$Javassist")
                .setInterfaces(RScoped.class.getName())
                .setSuperClass(Person.class.getName())
                .addField(String.format("private %s rValue;", RValue.class.getName()))
                .addMethod("public void commit() { rValue.set(this); }")
                .addMethod(String.format("public void associate(%s rObject) { this.rValue = (%s) rObject; }", RAny.class.getName(), RValue.class.getName()))
                .toClass();

        final Object instance = BeanUtils.instantiateClass(clazz);

        Person person = (Person) instance;
        person.setAge(12);
        person.setCreatedAt(LocalDate.now());

        System.out.println(person);

        final var radiance = Radiance.create(SyncConnectionContext.create());

        RScoped scoped = (RScoped) instance;
        scoped.associate(radiance.getValue("Dagger", Person.class));
        scoped.commit();
    }
}
