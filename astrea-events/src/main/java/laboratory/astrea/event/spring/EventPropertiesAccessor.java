package laboratory.astrea.event.spring;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.PlaceholdersResolver;

import java.util.function.Supplier;

@RequiredArgsConstructor
public final class EventPropertiesAccessor {

    private final EventProperties eventProperties;

    private final PlaceholdersResolver placeholdersResolver;


    public String getPublisherName(){

        return eventProperties.getEventPublisher();
    }

    public String getTopicConsumerName(){
        return getProperty(eventProperties::getTopicConsumer);
    }

    public String getTopicRoutineKey(){
        return getProperty(eventProperties::getTopicRoutineKey);
    }

    public String getBroadcastConsumerName(){
        return getProperty(eventProperties::getBroadcastConsumer);
    }

    public String getBroadcastRoutineKey(){
        return getProperty(eventProperties::getBroadcastRoutineKey);
    }

    private String getProperty(Supplier<String> propertySupplier){
        return placeholdersResolver.resolvePlaceholders(propertySupplier.get()).toString();
    }
}
