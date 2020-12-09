package laboratory.astrea.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.bind.PlaceholdersResolver;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@Configuration
@Import(IntrinsicEventConfiguration.class)
public class SpringComponentAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    PlaceholdersResolver placeholdersResolver(Environment environment) {
        return new PropertySourcesPlaceholdersResolver(environment);
    }

}
