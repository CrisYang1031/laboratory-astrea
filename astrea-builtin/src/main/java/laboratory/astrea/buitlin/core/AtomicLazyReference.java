package laboratory.astrea.buitlin.core;

import io.vavr.Lazy;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class AtomicLazyReference<T> implements Supplier<T> {

    private final AtomicReference<Lazy<T>> lazyReference;

    private AtomicLazyReference(Supplier<T> supplier) {
        lazyReference = new AtomicReference<>(Lazy.of(supplier));
    }

    public static <T> AtomicLazyReference<T> of(Supplier<T> supplier) {

        return new AtomicLazyReference<>(supplier);
    }

    public static <T> AtomicLazyReference<T> of(T value) {

        return new AtomicLazyReference<>(() -> value);
    }

    @Override
    public T get() {
        return lazyReference.get().get();
    }

    public Lazy<T> swap(T value) {
        return swap(() -> value);
    }

    public Lazy<T> swap(Supplier<T> supplier) {
        return lazyReference.getAndSet(Lazy.of(supplier));
    }
}
