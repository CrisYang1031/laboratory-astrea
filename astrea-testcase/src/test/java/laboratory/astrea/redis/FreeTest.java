package laboratory.astrea.redis;

import laboratory.astrea.buitlin.core.Pair;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static laboratory.astrea.buitlin.core.Json.jsonString;

@Slf4j
public final class FreeTest {

    public static void main(String[] args) {

        final var pairMap = IntStream.range(0, 10)
                .mapToObj(operand -> Pair.of(operand, Integer.toString(operand)))
                .collect(Collectors.toMap(Pair::second, Function.identity()));

        System.out.println(jsonString(pairMap));

    }

}
