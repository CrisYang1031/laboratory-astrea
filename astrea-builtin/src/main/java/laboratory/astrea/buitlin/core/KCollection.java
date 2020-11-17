package laboratory.astrea.buitlin.core;


import io.vavr.collection.Array;
import org.checkerframework.checker.units.qual.A;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


@SuppressWarnings("unused")
public final class KCollection {

    private KCollection() {
        throw new AssertionError();
    }

    //-------------------  List  -----------------------
    @SafeVarargs
    public static <E> List<E> List(E... elements) {
        return listOf(elements).castToList();
    }

    @SafeVarargs
    public static <E> ImmutableList<E> listOf(E... elements) {

        return Lists.immutable.with(elements);
    }

    public static <E> ImmutableList<E> listOf(Iterable<E> iterable) {

        return Lists.immutable.withAll(iterable);
    }

    public static <E> ImmutableList<E> listOf(Stream<E> stream) {

        return Lists.immutable.fromStream(stream);
    }

    @SafeVarargs
    public static <E> MutableList<E> mutableListOf(E... elements) {

        return Lists.mutable.with(elements);
    }

    public static <E> MutableList<E> mutableListOf(Iterable<E> iterable) {

        return Lists.mutable.withAll(iterable);
    }


    //-------------------  Set  -----------------------

    @SafeVarargs
    public static <E> ImmutableSet<E> setOf(E... elements) {

        return Sets.immutable.with(elements);
    }


    @SafeVarargs
    public static <E> MutableSet<E> mutableSetOf(E... elements) {

        return Sets.mutable.with(elements);
    }

    public static <E> MutableSet<E> mutableSetOf(Iterable<E> iterable) {

        return Sets.mutable.withAll(iterable);
    }

    //-------------------  Map  -----------------------

    public static <K, V> Map<K, V> Map(K k1, V v1) {
        return Maps.immutable.with(k1, v1).castToMap();
    }


    public static <K, V, U> Map<K, U> mappingValue(Map<K, V> map, Function<? super V, ? extends U> mappingFunction) {

        final var hashMap = new HashMap<K, U>(map.size());

        map.forEach((k, v) -> hashMap.put(k, mappingFunction.apply(v)));

        return hashMap;

    }

    //-------------------  Stream  -----------------------

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T> Stream<T> stream(Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
    }

    public static <T> Stream<T> stream(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<T> optional) {
        return optional.stream();
    }

    @SafeVarargs
    public static <T> Stream<T> stream(Stream<T>... streams) {
        if (streams == null) return Stream.empty();
        if (streams.length == 1) return streams[0];
        return Arrays.stream(streams).flatMap(Function.identity());
    }


    //-------------------  Array  -----------------------

    public static <T> T[] array(Iterable<T> iterable, IntFunction<T[]> generator) {
        return stream(iterable).toArray(generator);
    }

    //-------------------  Others  -----------------------


    public static <T> List<T> fill(int size, T t) {
        return Array.fill(size, t).asJava();
    }

    public static <T> List<T> drain(Queue<T> queue) {
        final MutableList<T> list = mutableListOf();
        for (var t = queue.poll(); t != null; ) {
            list.add(t);
            t = queue.poll();
        }
        return list;
    }

    public static <T> Collector<T, ?, List<T>> toImmutableList() {

        return Collector.<T, MutableList<T>, List<T>>of(
                Lists.mutable::empty,
                MutableList::add,
                MutableList::withAll,
                ts -> ts.toImmutable().castToList());
    }


    public static <T> Collector<T, ?, Set<T>> toImmutableSet() {

        return Collector.<T, MutableSet<T>, Set<T>>of(
                Sets.mutable::empty,
                MutableSet::add,
                MutableSet::withAll,
                ts -> ts.toImmutable().castToSet(),
                Collector.Characteristics.UNORDERED);
    }

}