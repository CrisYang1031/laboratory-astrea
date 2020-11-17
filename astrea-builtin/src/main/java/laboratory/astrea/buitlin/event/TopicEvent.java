package laboratory.astrea.buitlin.event;

import lombok.Value;

@Value(staticConstructor = "of")
public class TopicEvent<Topic, Content> {

    Topic topic;

    Content content;
}
