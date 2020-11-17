package laboratory.astrea.buitlin.event;

public interface TopicEventSender {

    void send(TopicEvent<?, ?> topicEvent);
}
