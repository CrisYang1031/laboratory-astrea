package laboratory.astrea.buitlin.event;

import lombok.Getter;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface TopicEvent<Topic, Content> {


    Topic getTopic();


    Content getContent();


    Duration delay();


    static <Topic, Content> TopicEvent<Topic, Content> of(Topic topic, Content content) {
        return new Companion<>(topic, content);
    }

    @Getter
    class Companion<Topic, Content> implements TopicEvent<Topic, Content> {

        private final Topic topic;

        private final Content content;

        private final Duration delay;

        public Companion(Topic topic, Content content) {
            this.topic = topic;
            this.content = content;
            this.delay = Duration.ZERO;
        }

        @Override
        public Duration delay() {
            return delay;
        }

    }

}
