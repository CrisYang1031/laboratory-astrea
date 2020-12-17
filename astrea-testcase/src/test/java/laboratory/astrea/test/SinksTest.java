package laboratory.astrea.test;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.util.concurrent.Queues;

import java.util.function.Consumer;

@Slf4j
public final class SinksTest implements Consumer<Integer> {

    private int sum = 0;

    public static void main(String[] args) {

        final var queue = Queues.<Integer>unboundedMultiproducer().get();

        final Sinks.Many<Integer> unicastSink = Sinks.unsafe().many().unicast().onBackpressureBuffer(queue);

        final var consumer = new SinksTest();
        unicastSink.asFlux()
//                .log()
                .subscribe(consumer);


        final var list = Flux.range(0, 20)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(integer -> unicastSink.tryEmitNext(1))
                .sequential()
                .collectList()
                .block();

        System.out.println(list);

        System.out.println("sum is " + consumer.sum);

    }

    @Override
    public void accept(Integer integer) {
        sum++;
    }
}
