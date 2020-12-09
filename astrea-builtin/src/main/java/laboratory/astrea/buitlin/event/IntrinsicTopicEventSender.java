package laboratory.astrea.buitlin.event;

import java.util.stream.Stream;

import static laboratory.astrea.buitlin.core.KCollection.stream;

public final class IntrinsicTopicEventSender implements TopicEventSender {


    private final TopicEventDispatcher dispatcher;


    public IntrinsicTopicEventSender(Iterable<TopicEventDispatcher> dispatchers) {

        this(stream(dispatchers));
    }

    public IntrinsicTopicEventSender(Stream<TopicEventDispatcher> dispatchers){

        dispatcher = dispatchers
                .sorted()
                .reduce(TopicEventDispatcher::next)
                .orElseThrow();
    }

    @Override
    public void send(TopicEvent<?, ?> topicEvent) {

        dispatcher.dispatch(topicEvent);
    }
}
