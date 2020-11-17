package laboratory.astrea.buitlin.event;


@FunctionalInterface
public interface TopicEventDispatcher {

    TopicEventReceiver<?, ?> dispatch(TopicEvent<?, ?> topicEvent);

    default TopicEventDispatcher next(TopicEventDispatcher nextDispatcher) {

        return topicEvent -> {

            final var receiver = this.dispatch(topicEvent);

            if (receiver == TopicEventReceiver.NoReceiver()) {
                return nextDispatcher.dispatch(topicEvent);
            }
            return receiver;
        };
    }

}
