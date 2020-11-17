package laboratory.astrea.buitlin.reactive;

import lombok.extern.slf4j.Slf4j;
import reactor.core.CoreSubscriber;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static laboratory.astrea.buitlin.reactive.Reactors.launch;


@Slf4j
public class CoroutineScopeEventSink<T> {

    private final Queue<T> queue = Reactors.mpQueue();

    private volatile int wip;

    @SuppressWarnings("rawtypes")
    private static final AtomicIntegerFieldUpdater<CoroutineScopeEventSink> WIP =
            AtomicIntegerFieldUpdater.newUpdater(CoroutineScopeEventSink.class, "wip");

    private final CoreSubscriber<? super T> actual;

    public CoroutineScopeEventSink(CoreSubscriber<? super T> actual) {
        this.actual = actual;
    }


    public void next(T t) {
        queue.offer(t);
        if (WIP.getAndIncrement(this) == 0)
            launch(this::drainRegular);
    }

    private void drainRegular() {

        int missed = 1;

        do {

            for (; ; ) {

                final T t = this.queue.poll();

                boolean empty = t == null;

                if (empty) {
                    break;
                }

                actual.onNext(t);
            }

        } while ((missed = WIP.addAndGet(this, -missed)) != 0);

        log.info("drainRegular done!");
    }

}
