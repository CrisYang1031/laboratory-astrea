package laboratory.astrea.redis.api;

import java.time.Duration;
import java.time.LocalDateTime;

public interface RExpirable {



    void expire(Duration duration);



    void expireAt(LocalDateTime expireAt);



    void clearExpire();



    Duration remainTimeToLive();
}
