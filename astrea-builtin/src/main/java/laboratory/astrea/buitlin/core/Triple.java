package laboratory.astrea.buitlin.core;

import lombok.EqualsAndHashCode;
import reactor.function.Consumer3;
import reactor.function.Function3;

@EqualsAndHashCode
public final class Triple<T, R, V> {

    private static final Triple<?, ?, ?> EMPTY = Triple.of(null, null, null);

    public final T first;

    public final R second;

    public final V third;

    private Triple(T first, R second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <T, R, V> Triple<T, R, V> of(T first, R second, V third) {
        return new Triple<>(first, second, third);
    }

    public static <T, R, V> Triple<T, R, V> empty() {
        //noinspection unchecked
        return (Triple<T, R, V>) EMPTY;
    }


    public void decompose(Consumer3<T, R, V> decomposer) {
        decomposer.accept(first, second, third);
    }

    public <U> U decompose(Function3<T, R, V, U> decomposer) {
        return decomposer.apply(first, second, third);
    }

    public T first() {
        return first;
    }

    public R second() {
        return second;
    }

    public V third() {
        return third;
    }


    public <U> Triple<U, R, V> withFirst(U first) {
        return new Triple<>(first, second, third);
    }

    public <U> Triple<T, U, V> withSecond(U second) {
        return new Triple<>(first, second, third);
    }

    public <U> Triple<T, R, U> withThird(U third) {
        return new Triple<>(first, second, third);
    }


    @Override
    public String toString() {
        return "Triple{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }
}
