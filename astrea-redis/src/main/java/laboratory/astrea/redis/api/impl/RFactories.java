package laboratory.astrea.redis.api.impl;

import laboratory.astrea.redis.api.RFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RFactories {

    Javassist(new JavassistRFactory()),

    ;

    @Getter
    private final RFactory factory;

}
