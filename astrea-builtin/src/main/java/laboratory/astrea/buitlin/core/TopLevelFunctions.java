package laboratory.astrea.buitlin.core;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public final class TopLevelFunctions {

    private final static HashFunction MURMUR_3_128 = Hashing.murmur3_128();

    private final static HashFunction MURMUR_3_32 = Hashing.murmur3_32();

    private TopLevelFunctions() {
        throw new AssertionError("Suppress default constructor for non instantiated ability");
    }

    public static String hashing(String value) {
        return MURMUR_3_128.newHasher().putString(value, StandardCharsets.UTF_8).hash().toString();
    }

    public static String hashing_32(String value) {
        return MURMUR_3_32.newHasher().putString(value, StandardCharsets.UTF_8).hash().toString();
    }

    public static <T> T cast(Object value) {
        //noinspection unchecked
        return (T) value;
    }

    public static void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            sneakThrow(e);
        }
    }

    public static <T extends Throwable, R> R sneakThrow(Throwable throwable) throws T {
        //noinspection unchecked
        throw (T) throwable;
    }

    public static long nanoTime() {
        return System.nanoTime();
    }

}
