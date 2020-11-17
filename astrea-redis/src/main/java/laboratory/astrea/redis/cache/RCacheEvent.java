package laboratory.astrea.redis.cache;


import laboratory.astrea.buitlin.core.Pair;

import java.util.function.Function;

public enum RCacheEvent implements Function<String, Pair<RCacheEvent, String>> {

    Delete,

    DeleteAll,

    Update,

    UpdateAll,
    ;

    @Override
    public Pair<RCacheEvent, String> apply(String s) {
        return Pair.of(this, s);
    }
}
