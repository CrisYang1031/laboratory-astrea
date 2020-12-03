package laboratory.astrea.event.spring;

import com.rabbitmq.client.Channel;
import laboratory.astrea.spring.SpringComponentAutoConfiguration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnClass({RabbitTemplate.class, Channel.class})
@EnableConfigurationProperties(value = EventProperties.class)
@Import(RabbitEventAutoConfiguration.class)
@AutoConfigureAfter(value = SpringComponentAutoConfiguration.class)
@ConditionalOnExpression(value = "'${spring.profiles.include}'.contains('rabbitmq')")
public class EventAutoConfiguration {

}
