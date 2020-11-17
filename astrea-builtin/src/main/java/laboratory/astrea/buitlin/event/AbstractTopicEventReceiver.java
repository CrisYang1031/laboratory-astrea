package laboratory.astrea.buitlin.event;


import io.vavr.Lazy;
import io.vavr.control.Try;
import laboratory.astrea.buitlin.reactive.CoroutineScopeEventSink;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.UnicastProcessor;

import java.util.function.Consumer;

@Slf4j
public abstract class AbstractTopicEventReceiver<topic, content> implements TopicEventReceiver<topic, content>, Consumer<TopicEvent<topic, content>> {

    private final Lazy<CoroutineScopeEventSink<TopicEvent<topic, content>>> sinkLazy = Lazy.of(this::initialEventSink);

    @Override
    public final void receive(TopicEvent<topic, content> topicEvent) {
        log.info("INFO#ReceiveEvent - {}", topicEvent.getTopic());
        sinkLazy.get().next(topicEvent);
    }

    private CoroutineScopeEventSink<TopicEvent<topic, content>> initialEventSink() {

        /*
         * 这里无需使用 多生产-单消费者队列
         * 因为FluxProcessor在创建FluxSink的时候 -> new FluxCreate.SerializedSink<>(s) 在代理层已经使用了mpscQueue作为缓冲队列
         * 当然这里没有选择Create FluxSink作为Publisher 而使用了CoroutineScopeEventSink作为数据生产源
         * CoroutineScopeEventSink同样使用mpscQueue作为缓冲队列 并且只负责生产next数据 并不作为FluxSink的一个implement
         */
        final var processor = UnicastProcessor.<TopicEvent<topic, content>>create();

        processor
                .subscribe(it -> Try.run(() -> accept(it))
                        .onFailure(throwable -> log.error("ERROR#Exception in receive event", throwable)));

        return new CoroutineScopeEventSink<>(processor);
    }


//    private static final Consumer<Throwable> THROWABLE_CONSUMER = throwable -> log.error("ERROR#Exception in receive event", throwable);

    //    private final Lazy<FluxSink<TopicEvent<topic, content>>> processorLazy = Lazy.of(this::initialProcessor);
//
//
//    @Override
//    public void receive(TopicEvent<topic, content> topicEvent) {
//        log.info("INFO#ReceiveEvent - {}", topicEvent.getTopic());
//        processorLazy.get().next(topicEvent);
//    }
//
//
//    private FluxSink<TopicEvent<topic, content>> initialProcessor() {
//
//        final var processor = UnicastProcessor.<TopicEvent<topic, content>>create();
//
//        processor.subscribe(CoroutineScopeOnNextSubscriber.with(this, THROWABLE_CONSUMER));
//
//        return processor.sink();
//    }


}
