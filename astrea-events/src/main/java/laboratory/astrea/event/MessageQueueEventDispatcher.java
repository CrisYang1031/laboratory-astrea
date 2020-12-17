package laboratory.astrea.event;

import laboratory.astrea.buitlin.event.TopicEvent;
import laboratory.astrea.buitlin.event.TopicEventDispatcher;
import laboratory.astrea.buitlin.event.TopicEventReceiver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MessageQueueEventDispatcher implements TopicEventDispatcher {

    private final MessageQueueEventReceiver<?> messageQueueEventReceiver;

    @Override
    public TopicEventReceiver<?, ?> dispatch(TopicEvent<?, ?> topicEvent) {

        if (topicEvent.getTopic() == MessageQueueTopicEvent.class) {

            return messageQueueEventReceiver;
        }

        return TopicEventReceiver.NoReceiver();
    }

    @Override
    public int priority() {
        return 10;
    }
}
