package laboratory.astrea.buitlin.core;

import io.vavr.CheckedConsumer;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedFunction1;
import io.vavr.CheckedRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static laboratory.astrea.buitlin.core.TopLevelFunctions.cast;
import static laboratory.astrea.buitlin.core.TopLevelFunctions.sneakThrow;

public final class Functions {

    private Functions() {
        throw new UnsupportedOperationException();
    }

    private final static Predicate<?> ALWAYS_TRUE_PREDICATE = o -> true;

    private final static Predicate<?> ALWAYS_FALSE_PREDICATE = o -> false;

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <S, T extends S> Predicate<S> instanceOf(Class<T> clazz, Predicate<T>... andPredicates) {

        final Predicate<S> predicate = clazz::isInstance;

        if (andPredicates == null) {
            return predicate;
        }

        if (andPredicates.length == 1) {
            return predicate.and(it -> andPredicates[0].test((T) it));
        }

        if (andPredicates.length == 2) {
            return predicate.and(it -> andPredicates[0].and(andPredicates[1]).test((T) it));
        }

        return predicate.and(it -> Arrays.stream(andPredicates).reduce(Predicate::and)
                .orElse(alwaysTrue())
                .test((T) it));
    }

    public static <T, R> Predicate<T> mappingAndTest(Function<T, R> mapping, Predicate<? super R> andTest) {
        return t -> andTest.test(mapping.apply(t));
    }


    public static <T> Predicate<T> is(T value) {
        return obj -> Objects.equals(obj, value);
    }


    public static <T, R, U> Function<T, R> mappingAndApply(Function<T, U> mapping, Function<? super U, R> andThen) {
        return t -> mapping.andThen(andThen).apply(t);
    }

    public static <T, R> Function<T, R> Function(CheckedFunction1<T, R> checkedFunction) {
        return t -> {
            try {
                return checkedFunction.apply(t);
            } catch (Throwable throwable) {
                return sneakThrow(throwable);
            }
        };
    }

    public static <T> Consumer<T> Consumer(CheckedConsumer<T> checkedConsumer) {
        return t -> {
            try {
                checkedConsumer.accept(t);
            } catch (Throwable throwable) {
                sneakThrow(throwable);
            }
        };
    }

    public static <T> T Try(CheckedFunction0<T> checkedFunction) {
        try {
            return checkedFunction.apply();
        } catch (Throwable throwable) {
            return sneakThrow(throwable);
        }
    }

    public static void Try(CheckedRunnable checkedRunnable) {
        try {
            checkedRunnable.run();
        } catch (Throwable throwable) {
            sneakThrow(throwable);
        }
    }

    @Contract(pure = true)
    @NotNull
    public static <T> Supplier<T> runnableSupplier(Runnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }

    public static <T> Predicate<T> alwaysTrue() {
        return cast(ALWAYS_TRUE_PREDICATE);
    }

    public static <T> Predicate<T> alwaysFalse() {
        return cast(ALWAYS_FALSE_PREDICATE);
    }

    public static <T> Predicate<T> oncePredicate() {

        return new Predicate<>() {

            private final AtomicBoolean once = new AtomicBoolean(false);

            @Override
            public boolean test(Object o) {
                return once.compareAndSet(false, true);
            }
        };
    }

    public static <T> Consumer<T> onceConsumer(Consumer<T> actual) {

        return new OnceConsumer<>() {
            @Override
            public void actualAccept(T t) {
                actual.accept(t);
            }
        };
    }

    public static Supplier<Void> NothingSupplier() {
        return () -> null;
    }

}
