package laboratory.astrea.redis;

import laboratory.astrea.buitlin.core.Pair;
import laboratory.astrea.redis.cache.RCache;
import laboratory.astrea.test.TestKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;

import java.time.Duration;
import java.time.LocalDateTime;

import static laboratory.astrea.buitlin.core.Strings.randomUpperCaseLetter;
import static laboratory.astrea.buitlin.core.TopLevelFunctions.delay;

@Slf4j
public final class RCacheTest {

    public static void main(String[] args) {

        TestKit.useRedisApplication(applicationContext -> {

            final var connectionContext = applicationContext.getBean(SyncConnectionContext.class);

            final var typeReference = new ParameterizedTypeReference<Pair<String, LocalDateTime>>() {
            };

            final var cacheName = "lilith";

            final var rCache = RCache.<Pair<String, LocalDateTime>>builder()
                    .cacheName(cacheName)
                    .connectionContext(connectionContext)
                    .timeout(Duration.ofSeconds(20))
                    .caffeineCustomizer(caffeine -> caffeine.removalListener(
                            (key, value, cause) -> log.info("INFO#RCache[{}] - {} has been removed, cause: {}", cacheName, key, cause)))                    .type(typeReference)
                    .build();

            rCache.put("test key", Pair.of(randomUpperCaseLetter(), LocalDateTime.now()));

            delay(1000);

            rCache.put("test key2", Pair.of(randomUpperCaseLetter(), LocalDateTime.now()));

            delay(1000);

        });


    }
}
