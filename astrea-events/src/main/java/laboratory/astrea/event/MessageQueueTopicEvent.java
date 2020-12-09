package laboratory.astrea.event;

import laboratory.astrea.buitlin.event.TopicEvent;

public interface MessageQueueTopicEvent<T, C> extends TopicEvent<T, C> {


    PublishType publishType();


    static <T, C> TopicEvent<Object, MessageQueueTopicEvent<T, C>> topicEvent(T topic, C content) {

        final var messageQueueEvent = new Companion<>(topic, content, PublishType.TOPIC);

        return TopicEvent.of(MessageQueueTopicEvent.class, messageQueueEvent);
    }


    static <T, C> TopicEvent<Object, MessageQueueTopicEvent<T, C>> broadcastEvent(T topic, C content) {

        final var messageQueueEvent = new Companion<>(topic, content, PublishType.BROADCAST);

        return TopicEvent.of(MessageQueueTopicEvent.class, messageQueueEvent);
    }


    class Companion<Topic, Content> extends TopicEvent.Companion<Topic, Content> implements MessageQueueTopicEvent<Topic, Content> {

        private final PublishType publishType;

        Companion(Topic topic, Content content, PublishType publishType) {
            super(topic, content);
            this.publishType = publishType;
        }


        @Override
        public PublishType publishType() {
            return publishType;
        }
    }

}
