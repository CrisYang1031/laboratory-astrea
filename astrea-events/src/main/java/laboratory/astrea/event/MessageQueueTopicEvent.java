package laboratory.astrea.event;

import laboratory.astrea.buitlin.event.TopicEvent;

public interface MessageQueueTopicEvent<C> extends TopicEvent<String, C> {


    PublishType publishType();


    static <T, C> TopicEvent<Object, MessageQueueTopicEvent<C>> topicEvent(String topic, C content) {

        final var messageQueueEvent = new Companion<>(topic, content, PublishType.TOPIC);

        return TopicEvent.of(MessageQueueTopicEvent.class, messageQueueEvent);
    }


    static <T, C> TopicEvent<Object, MessageQueueTopicEvent<C>> broadcastEvent(String topic, C content) {

        final var messageQueueEvent = new Companion<>(topic, content, PublishType.BROADCAST);

        return TopicEvent.of(MessageQueueTopicEvent.class, messageQueueEvent);
    }


    class Companion<Content> extends TopicEvent.Companion<String, Content> implements MessageQueueTopicEvent<Content> {

        private final PublishType publishType;

        Companion(String topic, Content content, PublishType publishType) {
            super(topic, content);
            this.publishType = publishType;
        }


        @Override
        public PublishType publishType() {
            return publishType;
        }
    }

}
