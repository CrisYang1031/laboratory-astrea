package laboratory.astrea.redis;

import laboratory.astrea.redis.api.RScope;
import laboratory.astrea.redis.api.RValue;

public final class PersonRScope extends Person implements RScope {

    private final RValue<Person> rValue;

    public PersonRScope(RValue<Person> rValue) {
        this.rValue = rValue;
    }

    @Override
    public void commit() {
        rValue.set(this);
    }
}
