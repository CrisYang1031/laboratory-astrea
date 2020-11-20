package laboratory.astrea.redis.api.impl;

import laboratory.astrea.redis.api.RProxyFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RFactories {

    Javassist(new JavassistRProxyFactory()),

    ;

    @Getter
    private final RProxyFactory factory;

}
