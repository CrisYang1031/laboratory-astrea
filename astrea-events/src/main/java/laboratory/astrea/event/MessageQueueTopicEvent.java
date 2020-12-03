package laboratory.astrea.event;

import laboratory.astrea.buitlin.event.TopicEvent;

public interface MessageQueueTopicEvent<T, C> extends TopicEvent<T, C> {


    default PublishType publishType() {
        return PublishType.TOPIC;
    }


    enum PublishType {

        TOPIC,

        BROADCAST,

        ;
    }

}
