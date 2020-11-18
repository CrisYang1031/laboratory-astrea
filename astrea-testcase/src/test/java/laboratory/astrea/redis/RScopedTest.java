package laboratory.astrea.redis;


import laboratory.astrea.redis.api.Radiance;
import laboratory.astrea.test.TestKit;

import java.time.LocalDate;

public class RScopedTest {

    public static void main(String[] args) {

        TestKit.useRedisApplication(applicationContext -> {

            final var radiance = Radiance.create(SyncConnectionContext.create());

            radiance.withScope(() -> {

                final var person = radiance.scopedValue("TensorFlow", Person.class);

                System.out.println(person);

                person.setAge(person.getAge() + 2);
                person.setCreatedAt(LocalDate.now());
                person.setName("TensorFlow");

            });


            radiance.withScope(() -> {

                final var person = radiance.scopedValue("TensorFlow", Person.class);

                System.out.println(person);

                person.setAge(person.getAge() + 2);
                person.setCreatedAt(LocalDate.now());
                person.setName("TensorFlow");

            });

        });




    }
}
