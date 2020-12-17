package laboratory.astrea.buitlin.reactive;

import lombok.experimental.Delegate;
import reactor.core.publisher.Sinks;

public abstract class ManySinkDelegate<T> implements Sinks.Many<T> {

    @Delegate
    protected final Sinks.Many<T> manySink;

    protected ManySinkDelegate(Sinks.Many<T> manySink) {
        this.manySink = manySink;
    }
}
