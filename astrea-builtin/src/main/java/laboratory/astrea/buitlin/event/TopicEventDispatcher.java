package laboratory.astrea.buitlin.event;


import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface TopicEventDispatcher extends Comparable<TopicEventDispatcher> {


    TopicEventReceiver<?, ?> dispatch(TopicEvent<?, ?> topicEvent);


    default int priority() {
        return 99;
    }

    @Override
    default int compareTo(@NotNull TopicEventDispatcher other) {
        return this.priority() - other.priority();
    }

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
