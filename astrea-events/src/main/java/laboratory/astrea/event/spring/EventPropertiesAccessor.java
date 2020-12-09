package laboratory.astrea.event.spring;

import org.springframework.boot.context.properties.bind.PlaceholdersResolver;

import java.util.function.Supplier;

public final class EventPropertiesAccessor {

    private final EventProperties eventProperties;

    private final PlaceholdersResolver placeholdersResolver;

    private final String topicConsumerName;

    private final String topicRoutineKey;

    private final String broadcastConsumerName;

    private final String broadcastRoutineKey;

    public EventPropertiesAccessor(EventProperties eventProperties, PlaceholdersResolver placeholdersResolver) {
        this.eventProperties = eventProperties;
        this.placeholdersResolver = placeholdersResolver;
        this.topicConsumerName = getProperty(eventProperties::getTopicConsumer);
        this.topicRoutineKey = getProperty(eventProperties::getTopicRoutineKey);
        this.broadcastConsumerName = getProperty(eventProperties::getBroadcastConsumer);
        this.broadcastRoutineKey = getProperty(eventProperties::getBroadcastRoutineKey);
    }

    public String getPublisherName() {
        return eventProperties.getEventPublisher();
    }

    public String getTopicConsumerName() {
        return topicConsumerName;
    }

    public String getTopicRoutineKey() {
        return topicRoutineKey;
    }

    public String getBroadcastConsumerName() {
        return broadcastConsumerName;
    }

    public String getBroadcastRoutineKey() {
        return broadcastRoutineKey;
    }

    private String getProperty(Supplier<String> propertySupplier) {
        return placeholdersResolver.resolvePlaceholders(propertySupplier.get()).toString();
    }
}
