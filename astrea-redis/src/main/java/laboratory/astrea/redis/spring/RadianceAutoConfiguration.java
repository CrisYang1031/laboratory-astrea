package laboratory.astrea.redis.spring;


import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.ClientResources;
import laboratory.astrea.redis.RedisSyncOperationFactory;
import laboratory.astrea.redis.SyncConnectionContext;
import laboratory.astrea.redis.api.Radiance;
import laboratory.astrea.redis.operation.LockOperation;
import laboratory.astrea.redis.operation.PubSubOperation;
import laboratory.astrea.redis.operation.ValueOperation;
import laboratory.astrea.redis.operation.ValueOperationExtension;
import laboratory.astrea.redis.spring.cache.RCacheableAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisClient.class)
public class RadianceAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public SyncConnectionContext syncConnectionContext(RedisProperties redisProperties, ClientResources clientResources) {

        final var host = redisProperties.getHost();
        final var port = redisProperties.getPort();
        final var password = Optional.ofNullable(redisProperties.getPassword()).orElse("");
        final var database = redisProperties.getDatabase();

        final var redisURI = RedisURI.builder()
                .withHost(host)
                .withPort(port)
                .withPassword(password)
                .withDatabase(database)
                .build();

        return SyncConnectionContext.create(redisURI, clientResources);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisSyncOperationFactory redisSyncOperationFactory(SyncConnectionContext syncConnectionContext) {
        return syncConnectionContext.syncOperationFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public ValueOperation valueOperation(RedisSyncOperationFactory redisSyncOperationFactory) {
        return redisSyncOperationFactory.valueOperation();
    }

    @Bean
    @ConditionalOnMissingBean
    public LockOperation lockOperation(RedisSyncOperationFactory redisSyncOperationFactory) {
        return redisSyncOperationFactory.lockOperation();
    }

    @Bean
    @ConditionalOnMissingBean
    public ValueOperationExtension valueOperationExtension(RedisSyncOperationFactory redisSyncOperationFactory) {
        return redisSyncOperationFactory.valueExtension();
    }

    @Bean
    @ConditionalOnMissingBean
    public PubSubOperation pubSubOperation(RedisSyncOperationFactory redisSyncOperationFactory) {
        return redisSyncOperationFactory.pubSubOperation();
    }

    @Bean
    @ConditionalOnMissingBean
    public Radiance radiance(SyncConnectionContext syncConnectionContext) {
        return Radiance.create(syncConnectionContext);
    }

    @Bean
    public RCacheableAdvisor rCacheableAdvisor(ApplicationContext applicationContext) {
        return new RCacheableAdvisor(applicationContext);
    }

}
