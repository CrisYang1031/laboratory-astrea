package laboratory.astrea.redis.spring.cache;

import laboratory.astrea.redis.cache.KeyTransformer;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

import static laboratory.astrea.buitlin.core.Json.jsonString;


@FunctionalInterface
public interface MethodInvocationKeyTransformer extends KeyTransformer<MethodInvocation> {

    default String apply(MethodInvocation methodInvocation) {
        return apply(methodInvocation.getMethod(), methodInvocation.getArguments());
    }

    String apply(Method method, Object[] arguments);


    enum Companion implements MethodInvocationKeyTransformer {

        Instance;

        @Override
        public String apply(Method method, Object[] arguments) {
            return method.getName() + "#" + jsonString(arguments);
        }
    }
}
