package laboratory.astrea.event;

import org.springframework.amqp.core.Message;
import org.springframework.core.ParameterizedTypeReference;

public interface MessageCarrier {


    Message message();


    <T> T payload(Class<T> clazz);


    <T> T payload(ParameterizedTypeReference<T> typeReference);


    void ack();


    void reject();

}
