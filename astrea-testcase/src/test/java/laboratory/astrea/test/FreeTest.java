package laboratory.astrea.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import laboratory.astrea.buitlin.core.TopLevelFunctions;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.util.concurrent.Queues;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.READ;
import static laboratory.astrea.buitlin.core.TopLevelFunctions.delay;

@Slf4j
public final class FreeTest {

    public static void main(String[] args) throws Exception {

        final var threadPoolExecutor = new ThreadPoolExecutor(1, 10, 10, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(1000), Executors.defaultThreadFactory(),
                (r, executor) -> {
                });

        IntStream.range(0, 10).forEach(value -> threadPoolExecutor.execute(() -> delay(10000)));


        IntStream.range(0, 10)
                .forEach(value -> {

                    delay(1000);

                    System.out.println(threadPoolExecutor.getActiveCount());
                    System.out.println(threadPoolExecutor.getPoolSize());
                    System.out.println(threadPoolExecutor.getTaskCount());
                    System.out.println("==".repeat(10));
                });



    }
}
