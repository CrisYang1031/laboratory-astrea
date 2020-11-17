package laboratory.astrea.redis;


import laboratory.astrea.redis.api.RScoped;
import laboratory.astrea.redis.api.RValue;

public final class PersonRScoped extends Person implements RScoped {

    private RValue<Person> rValue;

    @Override
    public void commit() { rValue.set(this); }

    @Override
    public void associate(Object rObject) {
        //noinspection unchecked
        this.rValue = (RValue<Person>) rObject;
    }
}
