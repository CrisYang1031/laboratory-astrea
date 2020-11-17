package laboratory.astrea.redis.operation;

import java.time.Duration;
import java.time.LocalDateTime;

public interface ExpireOperation {


    void expire(String key, Duration duration);


    void expireAt(String key, LocalDateTime expireAt);


    void clearExpire(String key);


    Duration remainTimeToLive(String key);
}
