package laboratory.astrea.redis;

import laboratory.astrea.test.TestKit;

public final class RCacheTest {

    public static void main(String[] args) {

        TestKit.useRedisApplication(applicationContext -> {

            final var connectionContext = applicationContext.getBean(SyncConnectionContext.class);




        });


    }
}
