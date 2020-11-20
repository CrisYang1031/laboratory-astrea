package laboratory.astrea.redis.api.impl;

import laboratory.astrea.redis.api.RCounter;
import laboratory.astrea.redis.codec.LongStringCodec;
import laboratory.astrea.redis.codec.StringCodec;
import laboratory.astrea.redis.operation.CommonOperation;
import laboratory.astrea.redis.operation.ValueOperationExtension;

public final class RCounterImpl extends RCommonImpl implements RCounter {

    private final ValueOperationExtension valueOperation;

    private final StringCodec longCodec = LongStringCodec.Companion;

    protected RCounterImpl(String name, ValueOperationExtension valueOperation) {
        super(name);
        this.valueOperation = valueOperation;
    }

    @Override
    public Long get() {
        return longCodec.decode(valueOperation.get(name), Long.class);
    }

    @Override
    public Long increase() {
        return valueOperation.increment(name);
    }

    @Override
    public Long increase(long amount) {
        return valueOperation.increment(name, amount);
    }

    @Override
    public Long decrease() {
        return valueOperation.decrement(name);
    }

    @Override
    public Long decrease(long amount) {
        return valueOperation.decrement(name, amount);
    }

    @Override
    protected CommonOperation commonOperation() {
        return valueOperation;
    }
}
