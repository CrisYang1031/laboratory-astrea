package laboratory.astrea.redis.api.impl;


import laboratory.astrea.redis.api.RAny;
import laboratory.astrea.redis.api.RExpirable;
import laboratory.astrea.redis.operation.CommonOperation;

import java.time.Duration;
import java.time.LocalDateTime;

abstract class RCommonImpl implements RAny, RExpirable {

    protected final String name;

    protected RCommonImpl(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void delete() {
        commonOperation().delete(name);
    }

    @Override
    public void unlink() {
        commonOperation().unlink(name);
    }

    @Override
    public void expire(Duration duration) {
        commonOperation().expire(name, duration);
    }

    @Override
    public void expireAt(LocalDateTime expireAt) {
        commonOperation().expireAt(name, expireAt);
    }

    @Override
    public void clearExpire() {
        commonOperation().clearExpire(name);
    }

    @Override
    public Duration remainTimeToLive() {
        return commonOperation().remainTimeToLive(name);
    }

    abstract protected CommonOperation commonOperation();

}
