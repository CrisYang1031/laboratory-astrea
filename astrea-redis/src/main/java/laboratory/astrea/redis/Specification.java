package laboratory.astrea.redis;

import io.lettuce.core.SetArgs;
import io.vavr.API;

import java.time.Duration;

import static io.vavr.API.*;
import static laboratory.astrea.redis.Specification.TimeoutDurationCase.*;

public interface Specification {

    String NONE_PATTERN = "^none_pattern";

    String ANONYMOUS_SUBSCRIBER = "^anonymous";

    SetArgs EMPTY_SET_ARGS = new SetArgs();

    Duration LOCK_TIMEOUT = Duration.ofSeconds(30);


    static boolean castResult(String result) {
        return "OK".equals(result);
    }


    static SetArgs setExArguments(Duration timeout) {

        final var timeoutDurationCase = from(timeout);

        return Match(timeoutDurationCase).of(
                Case(API.$(WITHOUT_TIMEOUT), it -> EMPTY_SET_ARGS),
                Case(API.$(WITH_NANOS), it -> SetArgs.Builder.px(timeout.toMillis())),
                Case(API.$(ONLY_SECOND), it -> SetArgs.Builder.ex(timeout.getSeconds()))
        );
    }

    static SetArgs setNxArguments(Duration timeout) {

        final var nx = SetArgs.Builder.nx();

        final var timeoutDurationCase = from(timeout);

        return Match(timeoutDurationCase).of(
                Case(API.$(WITHOUT_TIMEOUT), it -> nx),
                Case(API.$(WITH_NANOS), it -> nx.px(timeout.toMillis())),
                Case(API.$(ONLY_SECOND), it -> nx.ex(timeout.getSeconds()))
        );
    }

    enum TimeoutDurationCase {

        WITHOUT_TIMEOUT,

        WITH_NANOS,

        ONLY_SECOND,

        ;

        static TimeoutDurationCase from(Duration duration) {
            if (duration.isZero() || duration.isNegative()) {
                return WITHOUT_TIMEOUT;
            } else if (duration.getNano() == 0) {
                return ONLY_SECOND;
            } else {
                return WITH_NANOS;
            }
        }
    }
}
