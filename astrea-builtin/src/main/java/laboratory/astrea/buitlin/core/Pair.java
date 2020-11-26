package laboratory.astrea.buitlin.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@EqualsAndHashCode
public final class Pair<T, R> {

    private static final Pair<?, ?> EMPTY = Pair.of(null, null);

    @JsonProperty("_1")
    public final T first;

    @JsonProperty("_2")
    public final R second;

    private Pair(T first, R second) {
        this.first = first;
        this.second = second;
    }

    @JsonCreator
    public static <T, R> Pair<T, R> of(@JsonProperty("_1") T first, @JsonProperty("_2") R second) {
        return new Pair<>(first, second);
    }

    public static <T, R> Pair<T, R> empty() {
        //noinspection unchecked
        return (Pair<T, R>) EMPTY;
    }

    public static <T> List<T> toList(Pair<T, T> pair) {
        return List.of(pair.first, pair.second);
    }

    public void decompose(BiConsumer<T, R> decomposer) {
        decomposer.accept(first, second);
    }

    public <U> U decompose(BiFunction<T, R, U> decomposer) {
        return decomposer.apply(first, second);
    }

    public Map.Entry<T, R> toEntry() {
        return new AbstractMap.SimpleEntry<>(first, second);
    }

    public <U> Pair<U, R> withFirst(U first) {
        return new Pair<>(first, second);
    }

    public <U> Pair<T, U> withSecond(U second) {
        return new Pair<>(first, second);
    }

    public T first() {
        return first;
    }

    public R second() {
        return second;
    }

    public <U> U first(Function<T, U> mappingFunction) {
        return firstOptional().map(mappingFunction).orElse(null);
    }

    public <U> U second(Function<R, U> mappingFunction) {
        return secondOptional().map(mappingFunction).orElse(null);
    }

    public Optional<T> firstOptional() {
        return Optional.ofNullable(first);
    }

    public Optional<R> secondOptional() {
        return Optional.ofNullable(second);
    }

    public Pair<R, T> swap() {
        return of(second, first);
    }

    @Override
    public String toString() {
        return "{" + "_1=" + first + ", _2=" + second + '}';
    }
}
