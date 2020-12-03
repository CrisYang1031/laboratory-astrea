package laboratory.astrea.redis;

import laboratory.astrea.test.model.Person;
import laboratory.astrea.redis.api.RAny;
import laboratory.astrea.redis.api.RScoped;
import laboratory.astrea.redis.api.RValue;

import java.util.stream.IntStream;

import static laboratory.astrea.buitlin.core.TopLevelFunctions.delay;
import static laboratory.astrea.buitlin.instrument.InstrumentFactory.Javassist_;

public final class CleanerTest {


    public static void main(String[] args) {


        IntStream.range(0, 10).forEach(value -> {

            final var classInstrument = Javassist_.create("laboratory.astrea.redis.PersonRScope$Javassist" + value)
                    .setInterfaces(RScoped.class.getName())
                    .setSuperClass(Person.class.getName())
                    .addField(String.format("private %s rValue;", RValue.class.getName()))
                    .addMethod("public void commit() { rValue.set(this); }")
                    .addMethod(String.format("public void associate(%s rObject) { this.rValue = (%s) rObject; }", RAny.class.getName(), RValue.class.getName()));

            classInstrument.toClass();
        });


        for (int i = 1; i <= 1000; i++) {
            int[] a = new int[1000000];
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }

        System.gc();

        delay(5000);

    }
}
