package laboratory.astrea.spring;

import laboratory.astrea.buitlin.event.IntrinsicTopicEventSender;
import laboratory.astrea.buitlin.event.TopicEventDispatcher;
import laboratory.astrea.buitlin.event.TopicEventSender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntrinsicEventConfiguration {


    @Bean
    @ConditionalOnBean(value = TopicEventDispatcher.class)
    TopicEventSender intrinsicEventSender(ObjectProvider<TopicEventDispatcher> dispatchers) {

        return new IntrinsicTopicEventSender(dispatchers.stream());
    }

}
