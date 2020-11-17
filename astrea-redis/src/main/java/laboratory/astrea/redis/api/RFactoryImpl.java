package laboratory.astrea.redis.api;

public final class RFactoryImpl implements RFactory {

    @Override
    public <T> T proxyScopedValue(String name, Class<T> typeReference, RValue<T> rValue) {
        throw new UnsupportedOperationException();
    }
}
