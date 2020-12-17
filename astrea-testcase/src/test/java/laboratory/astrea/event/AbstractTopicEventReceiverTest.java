package laboratory.astrea.event;


import laboratory.astrea.buitlin.event.AbstractTopicEventReceiver;
import laboratory.astrea.buitlin.event.TopicEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static laboratory.astrea.buitlin.core.TopLevelFunctions.delay;
import static laboratory.astrea.buitlin.reactive.Reactors.coroutineScheduler;
import static laboratory.astrea.buitlin.reactive.Reactors.suspendScope;


@Slf4j
public final class AbstractTopicEventReceiverTest {

    public static void main(String[] args) {

        case_4();

    }


    private static void case_1() {
        final var receiver1 = createTopicEventReceiver();

        final var receiver2 = createTopicEventReceiver();

        executeOnNext(receiver2);

        executeOnNext(receiver1);

        delay(5000);
    }


    private static void case_2() {

        final var receiver = createTopicEventReceiver();

        suspendScope(() -> receiver.receive(TopicEvent.of("hello world", 1)));

        delay(5000);
    }

    private static void case_3() {
        final var receiver1 = createTopicEventReceiver();

        final var receiver2 = createTopicEventReceiver();

        executeOnNext3(receiver2);

        executeOnNext3(receiver1);

        delay(5000);
    }


    private static void case_4() {
        final var receiver = createTopicEventReceiver();

        final var scheduler = Schedulers.parallel();

        Flux.range(0, 100_000)
                .doOnNext(value -> scheduler.schedule(() -> receiver.receive(TopicEvent.of("hello world", value))))
                .doOnSubscribe(subscription -> log.info("start!"))
                .subscribe();

        delay(5000);
    }

    private static void executeOnNext(AbstractTopicEventReceiver<String, Integer> receiver) {

        final var scheduler = coroutineScheduler();

        Flux.range(0, 100)
                .doOnNext(value -> scheduler.schedule(() -> receiver.receive(TopicEvent.of("hello world", value))))
                .subscribe();

        Flux.range(100, 100)
                .doOnNext(value -> scheduler.schedule(() -> receiver.receive(TopicEvent.of("hello world", value))))
                .subscribe();
    }


    private static void executeOnNext3(AbstractTopicEventReceiver<String, Integer> receiver) {

        final var scheduler = Schedulers.parallel();

        Flux.range(0, 100)
                .doOnNext(value -> scheduler.schedule(() -> receiver.receive(TopicEvent.of("hello world", value))))
                .subscribe();

        Flux.range(100, 100)
                .doOnNext(value -> scheduler.schedule(() -> receiver.receive(TopicEvent.of("hello world", value))))
                .subscribe();
    }

    @NotNull
    private static AbstractTopicEventReceiver<String, Integer> createTopicEventReceiver() {

        return new AbstractTopicEventReceiver<>() {

            int sum = 0;

            final CountDownLatch latch = new CountDownLatch(1);

            {
                coroutineScheduler().schedule(latch::countDown, 1, TimeUnit.SECONDS);
                coroutineScheduler().schedule(() -> log.info("sum = {}", sum), 5, TimeUnit.SECONDS);
            }

            @SneakyThrows
            @Override
            public void accept(TopicEvent<String, Integer> topicEvent) {
                latch.await();
                sum++;
            }
        };
    }
}
