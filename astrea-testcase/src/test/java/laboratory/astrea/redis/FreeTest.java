package laboratory.astrea.redis;

import laboratory.astrea.buitlin.core.Json;
import laboratory.astrea.buitlin.core.Pair;
import laboratory.astrea.redis.cache.RCache;
import laboratory.astrea.redis.cache.RCacheEvent;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;


@Slf4j
public final class FreeTest {

    public static void main(String[] args) {

        final var content = """
                {"_1":"Update","_2":"interface java.util.List findPerson[\\"peter\\"]@PersonList,222244078690917"}
                """;


        final var stringPair = Json.jsonValue(content, RCache.R_CACHE_EVENT_MESSAGE_TYPE);

        System.out.println(stringPair);
        System.out.println(stringPair.first);
        System.out.println(stringPair.first.getClass());

    }


}
