package laboratory.astrea.buitlin.event;

import reactor.core.publisher.Mono;

public interface ReplyTopicEvent<Topic, Content> extends TopicEvent<Topic, Content> {


    void reply(Object result);


    <R> Mono<R> result();
}
