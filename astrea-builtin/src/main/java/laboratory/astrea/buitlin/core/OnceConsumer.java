package laboratory.astrea.buitlin.core;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class OnceConsumer<T> implements Consumer<T> {

    private Predicate<T> oncePredicate = Functions.oncePredicate();

    @Override
    public void accept(T t) {
        if (oncePredicate.test(t)) {
            actualAccept(t);
            oncePredicate = Functions.alwaysFalse();
        }
    }

    protected abstract void actualAccept(T t);
}
