package laboratory.astrea.redis;


import laboratory.astrea.test.model.Person;
import laboratory.astrea.test.model.Wrapped;
import laboratory.astrea.redis.api.Radiance;
import laboratory.astrea.test.TestKit;
import org.springframework.core.ParameterizedTypeReference;

import java.time.LocalDate;

public class RScopedTest {

    public static void main(String[] args) {

        TestKit.useRedisApplication(applicationContext -> {

            final var radiance = Radiance.create(SyncConnectionContext.create());

            radiance.withScope(() -> {

                final var typeReference = new ParameterizedTypeReference<Wrapped<Person>>() {
                };

                final var wrapped = radiance.scopedValue("wrapped TensorFlow", typeReference);

                System.out.println(wrapped);

                final var person = new Person();
                person.setAge(person.getAge() + 2);
                person.setCreatedAt(LocalDate.now());
                person.setName("TensorFlow");

                wrapped.setData(person);
                wrapped.setResult(true);
            });

            radiance.withScope(() -> {

                final var typeReference = new ParameterizedTypeReference<Wrapped<Person>>() {
                };

                final var wrapped = radiance.scopedValue("wrapped TensorFlow", typeReference);

                System.out.println(wrapped);

                final var person = wrapped.getData();
                person.setAge(person.getAge() + 2);
                person.setCreatedAt(LocalDate.now());
                person.setName("TensorFlow");

                wrapped.setData(person);
                wrapped.setResult(true);
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
