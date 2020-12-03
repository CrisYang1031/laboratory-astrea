package laboratory.astrea.event.spring;

import laboratory.astrea.buitlin.core.Functions;
import laboratory.astrea.buitlin.core.Json;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.SimplePropertyValueConnectionNameStrategy;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.bind.PlaceholdersResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

@Configuration(proxyBeanMethods = false)
public class RabbitEventAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public ConnectionNameStrategy connectionNameStrategy() {
        return new SimplePropertyValueConnectionNameStrategy("spring.application.name");
    }

    @Bean
    @ConditionalOnMissingBean(value = MessageConverter.class)
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter(Json.shared());
    }


    @Bean
    public EventPropertiesAccessor eventPropertiesAccessor(EventProperties eventProperties, PlaceholdersResolver placeholdersResolver) {
        return new EventPropertiesAccessor(eventProperties, placeholdersResolver);
    }

    private final Consumer<ApplicationStartedEvent> onceStartedEventConsumer = Functions.onceConsumer(startedEvent -> {

        final var applicationContext = startedEvent.getApplicationContext();
        final var declarables = applicationContext.getBean(Declarables.class);
        final var amqpAdmin = applicationContext.getBean(AmqpAdmin.class);

        declarables.getDeclarablesByType(Exchange.class).forEach(amqpAdmin::declareExchange);
        declarables.getDeclarablesByType(Queue.class).forEach(amqpAdmin::declareQueue);
        declarables.getDeclarablesByType(Binding.class).forEach(amqpAdmin::declareBinding);
    });

    @EventListener(value = ApplicationStartedEvent.class)
    public void rabbitComponentRegister(ApplicationStartedEvent startedEvent){
        onceStartedEventConsumer.accept(startedEvent);
    }

    @Bean
    public Declarables rabbitDeclarables(EventPropertiesAccessor propertiesAccessor){

        final var eventExchange = ExchangeBuilder.topicExchange(propertiesAccessor.getPublisherName())
                .durable(true)
                .build();

        final var topicConsumer = QueueBuilder.durable(propertiesAccessor.getTopicConsumerName())
                .build();


        final var topicBinding = BindingBuilder.bind(topicConsumer)
                .to(eventExchange)
                .with(propertiesAccessor.getTopicRoutineKey())
                .noargs();


        final var broadcastConsumerName = propertiesAccessor.getBroadcastConsumerName();

        final var actualBroadcastConsumerName = broadcastConsumerName + ".instance#" + ThreadLocalRandom.current().nextInt(10000);

        final var broadcastConsumer = QueueBuilder.nonDurable(actualBroadcastConsumerName)
                .exclusive()
                .autoDelete()
                .build();

        final var broadcastBinding = BindingBuilder.bind(broadcastConsumer)
                .to(eventExchange)
                .with(propertiesAccessor.getBroadcastRoutineKey())
                .noargs();


        return new Declarables(eventExchange, topicConsumer, topicBinding, broadcastConsumer, broadcastBinding);
    }

    @Bean
    public String[] queueNamesForListener(Declarables declarables){
        return declarables.getDeclarablesByType(Queue.class)
                .stream()
                .map(Queue::getName)
                .toArray(String[]::new);
    }
}
