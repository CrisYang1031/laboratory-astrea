package laboratory.astrea.test;

import laboratory.astrea.buitlin.core.Json;
import laboratory.astrea.event.spring.EventProperties;
import laboratory.astrea.event.spring.RabbitEventAutoConfiguration;
import lombok.Value;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@SpringBootApplication(proxyBeanMethods = false)
@RestController
public class AstreaApplication {

    private static final String PUBLISHER_NAME = "laboratory.event.publisher";
    private static final String ROUTING_KEY = "laboratory.topic.test-case";
    private static final String CONSUMER_NAME = "laboratory.topic.consumer.test-case";

    public static void main(String[] args) {
        SpringApplication.run(AstreaApplication.class);
    }


    @EventListener(value = ApplicationReadyEvent.class)
    public void onReady(ApplicationReadyEvent readyEvent) {

//        final var rabbitTemplate = readyEvent.getApplicationContext().getBean(RabbitTemplate.class);


//        final var amqpAdmin = readyEvent.getApplicationContext().getBean(AmqpAdmin.class);
//
//        final var topicExchange = ExchangeBuilder.topicExchange(PUBLISHER_NAME)
//                .durable(true)
//                .delayed()
//                .build();
//
//        final var topicQueue = QueueBuilder.durable(CONSUMER_NAME)
//                .build();
//
//        final var binding = BindingBuilder.bind(topicQueue)
//                .to(topicExchange)
//                .with(ROUTING_KEY)
//                .noargs();
//
//        amqpAdmin.declareExchange(topicExchange);
//        amqpAdmin.declareQueue(topicQueue);
//        amqpAdmin.declareBinding(binding);


//        final var receivedMessage = rabbitTemplate.receive(CONSUMER_NAME);


//        class Companion extends DefaultConsumer {
//
//
//            public Companion(Channel channel) {
//                super(channel);
//            }
//
//            @Override
//            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//
//                final var deliveryTag = envelope.getDeliveryTag();
//
//                System.out.println("deliveryTag : " + deliveryTag);
//
//                System.out.println(jsonValue(body, Person.class));
//
//                getChannel().basicAck(deliveryTag, true);
//            }
//        }

//        rabbitTemplate.execute(channel -> {
//
//            final var consumer = new Companion(channel);
//            channel.basicConsume(CONSUMER_NAME, false, consumer);
//
//            suspendDelay(Duration.ofSeconds(5), unchecked(() -> {
//                System.out.println("try to close connection");
//                consumer.getChannel().getConnection().close();
//            }));
//
//            return null;
//        });



//        System.out.println(receivedMessage.getMessageProperties().toString());
//
//        System.out.println(Strings.bytesToString(receivedMessage.getBody()));
//
//        final var person = Json.jsonValue(receivedMessage.getBody(), Person.class);
//        System.out.println(person);

//        MessagePropertiesBuilder.newInstance()
//                .setReplyTo()
//
//        IntStream.range(0, 2).forEach(value -> {
//            final var correlationData = new CorrelationData();
//            rabbitTemplate.convertAndSend(PUBLISHER_NAME, ROUTING_KEY, new Person("hello", LocalDateTime.now()), correlationData);
//        });

    }

    @Value
    static class Person {

        String name;

        LocalDateTime localDateTime;

    }


    @GetMapping
    Object echo() {
        return LocalDateTime.now();
    }

}
