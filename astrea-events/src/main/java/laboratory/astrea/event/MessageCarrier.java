package laboratory.astrea.event;

import org.springframework.amqp.core.Message;

public interface MessageCarrier {


    Message message();


    <T> T payload();


    void ack();


    void reject();

}
