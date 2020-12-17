package laboratory.astrea.buitlin.event;


import io.vavr.Lazy;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;

import static laboratory.astrea.buitlin.reactive.CoroutineScopes.coroutineScopeSink;

@Slf4j
public abstract class AbstractTopicEventReceiver<topic, content> implements TopicEventReceiver<topic, content>, Consumer<TopicEvent<topic, content>> {

    private final Lazy<Sinks.Many<TopicEvent<topic, content>>> sinkLazy = Lazy.of(this::initialEventSink);

    @Override
    public final void receive(TopicEvent<topic, content> topicEvent) {
//        log.debug("INFO#ReceiveEvent - {}", topicEvent.getTopic());
        sinkLazy.get().emitNext(topicEvent, (signalType, emitResult) -> {
            log.info("failure emit happened - {} {}", signalType, emitResult);
            return false;// do not retry
        });
    }

    private Sinks.Many<TopicEvent<topic, content>> initialEventSink() {

        final var eventSink = Sinks.unsafe().many().unicast().<TopicEvent<topic, content>>onBackpressureBuffer();

        eventSink.asFlux()
                .map(it -> Try.run(() -> accept(it)))
                .doOnNext(it -> it.onFailure(throwable -> log.error("ERROR#Exception in receive event", throwable)))
                .subscribe();

        return coroutineScopeSink(eventSink);
    }
}
