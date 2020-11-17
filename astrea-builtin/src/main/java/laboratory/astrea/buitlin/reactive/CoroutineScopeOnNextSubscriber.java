package laboratory.astrea.buitlin.reactive;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.BaseSubscriber;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Consumer;

import static laboratory.astrea.buitlin.reactive.Reactors.launch;

@Slf4j
public final class CoroutineScopeOnNextSubscriber<T> extends BaseSubscriber<T> implements CoreSubscriber<T> {

    private final Queue<T> queue = Reactors.mpQueue();

    private volatile int wip;

    @SuppressWarnings("rawtypes")
    private static final AtomicIntegerFieldUpdater<CoroutineScopeOnNextSubscriber> WIP =
            AtomicIntegerFieldUpdater.newUpdater(CoroutineScopeOnNextSubscriber.class, "wip");

    private final Consumer<? super T> actual;

    private final Consumer<Throwable> errorConsumer;


    private CoroutineScopeOnNextSubscriber(Consumer<? super T> actual, Consumer<Throwable> errorConsumer) {
        this.actual = actual;
        this.errorConsumer = errorConsumer;
    }

    public static <T> CoroutineScopeOnNextSubscriber<T> with(Consumer<? super T> actual) {
        return new CoroutineScopeOnNextSubscriber<>(actual, null);
    }

    public static <T> CoroutineScopeOnNextSubscriber<T> with(Consumer<? super T> actual, Consumer<Throwable> throwableConsumer) {
        return new CoroutineScopeOnNextSubscriber<>(actual, throwableConsumer);
    }

    @Override
    protected void hookOnNext(@NotNull T t) {
        queue.offer(t);
        if (WIP.getAndIncrement(this) == 0)
            launch(this::drainRegular);
    }

    private void drainRegular() {

        int missed = 1;

        do {
            try {
                for (; ; ) {

                    final T t = this.queue.poll();

                    boolean empty = t == null;

                    if (empty) {
                        break;
                    }

                    actual.accept(t);
                }
            } catch (Throwable throwable) {

                if (errorConsumer != null) errorConsumer.accept(throwable);
            }

        } while ((missed = WIP.addAndGet(this, -missed)) != 0);

        log.info("drainRegular done!");
    }
}
