package laboratory.astrea.buitlin.reactive;

import laboratory.astrea.buitlin.core.AtomicLazyReference;
import lombok.Value;
import lombok.With;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.concurrent.Queues;

import java.time.Duration;
import java.util.Queue;
import java.util.function.Supplier;

public final class Reactors {

    private static final AtomicLazyReference<Scheduler> COROUTINE_SCHEDULER_REFERENCE = AtomicLazyReference.of(Reactors::initialDefaultCoroutineScheduler);
    private static final String COROUTINE_SCHEDULER_NAME = "@coroutine";

    private Reactors() {
        throw new AssertionError("Suppress default constructor for non instantiated ability");
    }


    public static <T> @NotNull Mono<T> suspend(Supplier<? extends T> supplier) {

        return Mono.<T>fromSupplier(supplier).subscribeOn(COROUTINE_SCHEDULER_REFERENCE.get());
    }

    public static <T> @NotNull Mono<T> suspend(Runnable block) {

        return Mono.<T>fromRunnable(block).subscribeOn(COROUTINE_SCHEDULER_REFERENCE.get());
    }

    public static void launch(Runnable block) {
        COROUTINE_SCHEDULER_REFERENCE.get().schedule(block);
    }

    public static void suspendScope(Runnable block) {
        if (inSuspendedScope()) block.run();
        else suspend(block).subscribe();
    }


    public static <T> @NotNull Mono<T> suspendScope(Supplier<? extends T> supplier) {
        if (inSuspendedScope()) return Mono.fromSupplier(supplier);
        else return suspend(supplier);
    }

    public static void suspendDelay(Duration delay, Runnable block) {
        Mono.delay(delay, coroutineScheduler()).doOnNext(__ -> block.run()).subscribe();
    }

    public static Scheduler coroutineScheduler() {
        return COROUTINE_SCHEDULER_REFERENCE.get();
    }

    public static void swapScheduler(CoroutineSchedulerConfig schedulerConfig) {

        final var coroutineScheduler = buildCoroutineScheduler(schedulerConfig);

        final var previousScheduler = COROUTINE_SCHEDULER_REFERENCE.swap(coroutineScheduler);

        if (previousScheduler.isEvaluated()) {
            previousScheduler.get().dispose();
        }
    }

    @NotNull
    private static Scheduler buildCoroutineScheduler(CoroutineSchedulerConfig schedulerConfig) {
        return Schedulers.newBoundedElastic(schedulerConfig.threadCap, schedulerConfig.queuedTaskCap, COROUTINE_SCHEDULER_NAME, schedulerConfig.ttlSeconds);
    }

    public static void dispose() {
        COROUTINE_SCHEDULER_REFERENCE.get().dispose();
    }

    private static boolean inSuspendedScope() {
        return Thread.currentThread().getName().startsWith(COROUTINE_SCHEDULER_NAME);
    }

    private static Scheduler initialDefaultCoroutineScheduler() {

        final var schedulerConfig = CoroutineSchedulerConfig.DEFAULT;

        return buildCoroutineScheduler(schedulerConfig);
    }

    public static <T> Queue<T> mpQueue() {
        return Queues.<T>unboundedMultiproducer().get();
    }

    @Value
    public static class CoroutineSchedulerConfig {

        @With
        int threadCap;

        @With
        int queuedTaskCap;

        @With
        int ttlSeconds;

        public final static CoroutineSchedulerConfig DEFAULT = new CoroutineSchedulerConfig(32, 2048, 3600);
    }


}
