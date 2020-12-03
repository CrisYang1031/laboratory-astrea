package laboratory.astrea.test.service;

import laboratory.astrea.test.model.Person;
import laboratory.astrea.redis.cache.annotation.RCacheable;
import laboratory.astrea.redis.spring.cache.MethodInvocationKeyTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static laboratory.astrea.buitlin.core.Json.jsonString;

@Service
public class PersonService {

    @RCacheable(cacheName = "PersonList", ttl = "30m", keyTransformer = "customMethodInvocationKeyTransformer")
    public List<Person> findPerson(String personName) {
        System.out.println("find Person !!!");
        return List.of(new Person(personName, 12, LocalDate.now()), new Person(personName.repeat(2), 9, LocalDate.now().plusDays(3)));
    }

    @Bean
    MethodInvocationKeyTransformer customMethodInvocationKeyTransformer() {
        return (method, arguments) -> method.getReturnType() + " " + method.getName() + jsonString(arguments);
    }
}
