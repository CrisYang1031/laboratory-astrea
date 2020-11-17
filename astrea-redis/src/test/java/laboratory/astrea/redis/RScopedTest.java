package laboratory.astrea.redis;


import laboratory.astrea.buitlin.core.Json;
import laboratory.astrea.redis.api.Radiance;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication(proxyBeanMethods = false)
public class RScopedTest {


    public static void main(String[] args) {

        final var radiance = Radiance.create(SyncConnectionContext.create());

        final var rValue = radiance.getValue("person#RValue", Person.class);

        final var personRScope = new PersonRScope(rValue);

        personRScope.setAge(12);
        personRScope.setCreatedAt(LocalDate.now());
        personRScope.setName("Td");

        System.out.println(Json.jsonString(personRScope));


    }
}
