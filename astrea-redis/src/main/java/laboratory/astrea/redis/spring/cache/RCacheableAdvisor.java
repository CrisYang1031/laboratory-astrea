package laboratory.astrea.redis.spring.cache;

import io.vavr.control.Try;
import laboratory.astrea.redis.SyncConnectionContext;
import laboratory.astrea.redis.cache.RCache;
import laboratory.astrea.redis.cache.annotation.RCacheable;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public final class RCacheableAdvisor extends StaticMethodMatcherPointcutAdvisor {

    public RCacheableAdvisor(ApplicationContext applicationContext) {
        super(new RCacheableInterceptor(applicationContext));
    }

    @Override
    public boolean matches(@NotNull Method method, @NotNull Class<?> clazz) {
        return method.isAnnotationPresent(RCacheable.class);
    }

    @Override
    public Advice getAdvice() {
        return super.getAdvice();
    }


    private static class RCacheableInterceptor implements MethodInterceptor {

        private final ApplicationContext applicationContext;

        private final ConcurrentMap<String, RCache<Object>> cacheMap = new ConcurrentHashMap<>();

        private RCacheableInterceptor(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override
        public Object invoke(MethodInvocation methodInvocation) {

            final var method = methodInvocation.getMethod();
            final var rCacheable = method.getAnnotation(RCacheable.class);
            final var key = rCacheable.key();

            final RCache<Object> rCache = cacheMap.computeIfAbsent(rCacheable.cacheName(), cacheName -> produceRCache(method, rCacheable, cacheName));

            final var cacheKey = key.isEmpty() ? getMethodInvocationKeyTransformer(rCacheable.keyTransformer()).apply(methodInvocation) : key;

            return rCache.get(cacheKey, __ -> Try.of(methodInvocation::proceed).get());
        }

        public RCache<Object> produceRCache(Method method, RCacheable rCacheable, String cacheName) {

            if (method.getReturnType() == void.class || method.getReturnType() == Void.class) {
                throw new IllegalArgumentException("void class return type is not support for RCache");
            }

            final var connectionContextName = rCacheable.contextName();
            final var ttl = rCacheable.ttl();
            final var levelOneSpecification = rCacheable.levelOneSpecification();

            final var syncConnectionContext = connectionContextName.isEmpty()
                    ? applicationContext.getBean(SyncConnectionContext.class) : applicationContext.getBean(connectionContextName, SyncConnectionContext.class);

            final var timeout = ttl.isEmpty() ? Duration.ZERO : ApplicationConversionService.getSharedInstance().convert(ttl, Duration.class);

            return RCache.builder()
                    .cacheName(cacheName)
                    .timeout(timeout)
                    .connectionContext(syncConnectionContext)
                    .type(ParameterizedTypeReference.forType(method.getGenericReturnType()))
                    .caffeineSpecification(levelOneSpecification)
                    .caffeineCustomizer(caffeine -> caffeine.removalListener(
                            (key, value, cause) -> log.debug("INFO#RCache[{}] - {} has been removed, cause: {}", cacheName, key, cause)))
                    .build();
        }

        @NotNull
        public MethodInvocationKeyTransformer getMethodInvocationKeyTransformer(String keyTransformerName) {
            if (keyTransformerName.isEmpty()) {
                return MethodInvocationKeyTransformer.Companion.Instance;
            }
            return applicationContext.getBean(keyTransformerName, MethodInvocationKeyTransformer.class);
        }
    }
}
