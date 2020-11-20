package laboratory.astrea.redis;

import laboratory.astrea.redis.api.RAny;
import laboratory.astrea.redis.api.RScoped;

public final class WrappedPersonRScoped<T> extends Wrapped<T> implements RScoped {


    @Override
    public void commit() {

    }

    @Override
    public void associate(RAny rAny) {

    }
}
