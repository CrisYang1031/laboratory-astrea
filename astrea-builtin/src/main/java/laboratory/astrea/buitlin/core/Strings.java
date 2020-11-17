package laboratory.astrea.buitlin.core;

import com.google.common.base.Splitter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Strings {

    private final static List<String> UPPER_CASE_ALPHABETS = IntStream.rangeClosed('A', 'Z')
            .mapToObj(value -> String.valueOf((char) value))
            .collect(Collectors.toUnmodifiableList());

    private Strings() {
        throw new UnsupportedOperationException();
    }

    public static String repeat(int times, Supplier<String> supplierBlock) {
        final var stringBuilder = new StringBuilder();

        for (var i = 0; i < times; i++) {
            stringBuilder.append(supplierBlock.get());
        }
        return stringBuilder.toString();
    }

    public static List<String> splitAsList(CharSequence charSequence) {
        return splitAsList(charSequence, ',');
    }

    public static List<String> splitAsList(CharSequence charSequence, char separator) {
        return Splitter.on(separator)
                .trimResults()
                .omitEmptyStrings()
                .splitToList(charSequence);
    }

    public static String randomUpperCaseLetter() {
        return UPPER_CASE_ALPHABETS.get(ThreadLocalRandom.current().nextInt(UPPER_CASE_ALPHABETS.size()));
    }

    public static String joinedString(CharSequence... elements) {
        return String.join(",", elements);
    }

    public static String joinedString(Iterable<? extends CharSequence> elements) {
        return String.join(",", elements);
    }
}
