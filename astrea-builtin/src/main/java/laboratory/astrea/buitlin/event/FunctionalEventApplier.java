package laboratory.astrea.buitlin.event;

import java.util.function.Function;

public interface FunctionalEventApplier extends Function<Object, TopicEvent<?, ?>> {

    @Override
    default TopicEvent<?, ?> apply(Object value) {
        return TopicEvent.of(this, value);
    }
}
