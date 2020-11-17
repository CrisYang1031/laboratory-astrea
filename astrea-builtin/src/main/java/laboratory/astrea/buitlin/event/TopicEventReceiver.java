package laboratory.astrea.buitlin.event;

@FunctionalInterface
public interface TopicEventReceiver<topic, content> {


    void receive(TopicEvent<topic, content> topicEvent);


    static <T, C> TopicEventReceiver<T, C> NoReceiver() {
        return topicEvent -> {};
    }

    static <T, C> TopicEventReceiver<T, C> DeadLetter() {
        return topicEvent -> {};
    }
}
