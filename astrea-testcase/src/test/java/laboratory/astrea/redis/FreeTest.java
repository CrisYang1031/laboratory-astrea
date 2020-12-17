package laboratory.astrea.redis;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public final class FreeTest {

    public static void main(String[] args) {

        final var executorService = Executors.newCachedThreadPool();


        Flux.range(0, 10)
//                .parallel()
//                .runOn(Schedulers.boundedElastic())
                .log()
//                .sequential()
                .map(integer -> {
                    log.info("value is {}", integer);
                    return String.valueOf(integer);
                })
                .flatMap(s -> Flux.just(123,456))
                .blockLast();

    }
}
