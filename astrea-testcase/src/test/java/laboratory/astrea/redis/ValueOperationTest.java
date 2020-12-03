package laboratory.astrea.redis;

public final class ValueOperationTest {

    public static void main(String[] args) {

        final var connectionContext = SyncConnectionContext.create();

        final var valueOperation = RedisSyncOperationFactory.create(connectionContext).valueOperation();

        final var dagger = valueOperation.get("Dagger");

        System.out.println(dagger);
    }
}
