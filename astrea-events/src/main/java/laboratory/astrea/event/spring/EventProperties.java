package laboratory.astrea.event.spring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "events")
public final class EventProperties {

    private String eventPublisher = "laboratory.event.publisher";

    private String topicConsumer = "laboratory.topic.consumer.${spring.application.name}";

    private String topicRoutineKey = "laboratory.topic.${spring.application.name}";

    private String broadcastConsumer = "laboratory.broadcast.consumer.${spring.application.name}";

    private String broadcastRoutineKey = "laboratory.broadcast.${spring.application.name}";

}
