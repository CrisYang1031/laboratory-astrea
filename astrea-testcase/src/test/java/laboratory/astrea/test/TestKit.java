package laboratory.astrea.test;

import io.vavr.CheckedConsumer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
public final class TestKit {

    private TestKit() {
    }

    public static void useRedisApplication( @NotNull CheckedConsumer<ConfigurableApplicationContext> applicationContextConsumer) {

        TestKit.useApplication(TestKit::redisApplication, applicationContextConsumer);
    }

    private static ConfigurableApplicationContext redisApplication() {

        return new SpringApplicationBuilder(AstreaApplication.class)
                .web(WebApplicationType.NONE)
                .run("--spring.output.ansi.enabled=always",
                        "--logging.level.io.lettuce.core.RedisChannelHandler=info",
                        "--logging.level.io.lettuce.core=info",
                        "");
    }


    private static void useApplication(@NotNull Supplier<ConfigurableApplicationContext> contextSupplier
            , @NotNull CheckedConsumer<ConfigurableApplicationContext> contextConsumer) {
        try (var applicationContext = contextSupplier.get()) {
            contextConsumer.accept(applicationContext);
        } catch (Throwable throwable) {
            log.error("something wrong in running application", throwable);
        }
    }

    public static void awaitAlways() {
        new CompletableFuture<>().join();
    }


}
