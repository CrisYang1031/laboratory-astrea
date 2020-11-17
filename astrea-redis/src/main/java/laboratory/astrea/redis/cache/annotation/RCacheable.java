package laboratory.astrea.redis.cache.annotation;


import lombok.RequiredArgsConstructor;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RCacheable {


    String cacheName();


    String contextName() default "";


    String ttl() default "";


    String key() default "";


    String keyTransformer() default "";


    String levelOneSpecification() default "maximumSize=256, expireAfterWrite=10m";


    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    enum LevelOneSpecs {

        Singleton("maximumSize=1"),

        Balanced("maximumSize=256, expireAfterWrite=10m"),

        WeakValue("weakValues"),

        ;
        public final String Value;
    }

}