package laboratory.astrea.redis.pubsub;

import lombok.Value;

@Value(staticConstructor = "of")
public class RedisSubscriptionMessage {

    String pattern, channel, message;

}
