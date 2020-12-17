package laboratory.astrea.event;

import laboratory.astrea.buitlin.event.AbstractTopicEventReceiver;
import laboratory.astrea.buitlin.event.TopicEvent;
import laboratory.astrea.event.spring.EventPropertiesAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static io.vavr.API.*;

@RequiredArgsConstructor
public final class MessageQueueEventReceiver<C> extends AbstractTopicEventReceiver<Object, MessageQueueTopicEvent<C>> {

    private final EventPropertiesAccessor eventPropertiesAccessor;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void accept(TopicEvent<Object, MessageQueueTopicEvent<C>> topicEvent) {

        final var messageQueueEvent = topicEvent.getContent();

        Match(messageQueueEvent).of(
                Case($(this::isTopicEvent), it -> run(() -> sendTopicMessage(it))),
                Case($(this::isBroadcastEvent), it -> run(() -> sendBroadcastMessage(it)))
        );
    }


    private void sendTopicMessage(MessageQueueTopicEvent<C> messageQueueEvent) {

        publishEventMessage(messageQueueEvent, eventPropertiesAccessor.getTopicRoutineKey());
    }

    private void sendBroadcastMessage(MessageQueueTopicEvent<C> messageQueueEvent) {

        publishEventMessage(messageQueueEvent, eventPropertiesAccessor.getBroadcastRoutineKey());
    }

    private void publishEventMessage(MessageQueueTopicEvent<C> messageQueueEvent, String routineKey) {

        final var publisherName = eventPropertiesAccessor.getPublisherName();

        rabbitTemplate.convertAndSend(publisherName, routineKey, messageQueueEvent);
    }

    private boolean isBroadcastEvent(MessageQueueTopicEvent<C> event) {
        return event.publishType() == PublishType.BROADCAST;
    }

    private boolean isTopicEvent(MessageQueueTopicEvent<C> event) {
        return event.publishType() == PublishType.TOPIC;
    }

}
