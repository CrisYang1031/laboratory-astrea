package laboratory.astrea.redis;

import laboratory.astrea.test.service.PersonService;
import laboratory.astrea.test.TestKit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RCacheableTest {


    public static void main(String[] args) {

        TestKit.useRedisApplication(applicationContext -> {

            final var personService = applicationContext.getBean(PersonService.class);

            final var personList = personService.findPerson("peter");

            System.out.println(personList);

            final var personList2 = personService.findPerson("peter");

            System.out.println(personList2);

        });

    }
}
