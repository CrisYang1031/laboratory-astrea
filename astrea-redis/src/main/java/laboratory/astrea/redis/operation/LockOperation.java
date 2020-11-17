package laboratory.astrea.redis.operation;




import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Supplier;

import static laboratory.astrea.buitlin.core.Functions.runnableSupplier;

@SuppressWarnings("UnusedReturnValue")
public interface LockOperation {


    default void lock(String key, Duration timeout, Runnable runnableBlock) {
        lock(key, timeout, runnableSupplier(runnableBlock));
    }


    <T> T lock(String key, Duration timeout, Supplier<T> supplierBlock);



    //----------------------   static   ----------------------

    String LOCKING_KEY_SUFFIX = "^locking";


    static boolean isLockingKey(String key) {
        return key.endsWith(LOCKING_KEY_SUFFIX);
    }


    static String getOccupyName() {
        return Thread.currentThread().getName() + "@" + LocalTime.now();
    }


}
