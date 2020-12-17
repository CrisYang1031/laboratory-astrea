package laboratory.astrea.buitlin.reactive;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Sinks;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static laboratory.astrea.buitlin.reactive.Reactors.launch;
import static laboratory.astrea.buitlin.reactive.Reactors.mpQueue;

public final class CoroutineScopes {

    private CoroutineScopes() {
        throw new UnsupportedOperationException();
    }

    public static <T> Sinks.Many<T> coroutineScopeSink(Sinks.Many<T> manySink){
        if (manySink instanceof CoroutineScopeSink) return manySink;

        return new CoroutineScopeSink<>(manySink);
    }

    @Slf4j
    static class CoroutineScopeSink<T> extends ManySinkDelegate<T> implements Sinks.Many<T> {

        private final Queue<T> queue = mpQueue();

        private volatile int wip;

        @SuppressWarnings("rawtypes")
        private static final AtomicIntegerFieldUpdater<CoroutineScopeSink> WIP =
                AtomicIntegerFieldUpdater.newUpdater(CoroutineScopeSink.class, "wip");

        protected CoroutineScopeSink(Sinks.Many<T> manySink) {
            super(manySink);
        }

        @Override
        public void emitNext(@NotNull T t, @NotNull Sinks.EmitFailureHandler failureHandler) {
            queue.offer(t);
            if (WIP.getAndIncrement(this) == 0)
                launch(() -> this.drainRegular(failureHandler));
        }

        private void drainRegular(Sinks.EmitFailureHandler failureHandler) {

            int missed = 1;

            do {

                for (; ; ) {

                    final T t = this.queue.poll();

                    boolean empty = t == null;

                    if (empty) {
                        break;
                    }

                    super.emitNext(t, failureHandler);

                }

            } while ((missed = WIP.addAndGet(this, -missed)) != 0);

            log.info("drainRegular done!");
        }
    }
}
