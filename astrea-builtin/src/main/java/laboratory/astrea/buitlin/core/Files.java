package laboratory.astrea.buitlin.core;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.*;
import static laboratory.astrea.buitlin.core.Functions.Try;

@Slf4j
public final class Files {

    private static final String PATH_TO_SAVE;

    static {
        final var dir = System.getProperty("application.dir");
        PATH_TO_SAVE = dir == null ? "" : dir;
    }

    private Files() {
        throw new UnsupportedOperationException();
    }

    public static void appendLines(String filename, Collection<String> lines) {

        if (lines.isEmpty()) return;

        final var path = Paths.get(PATH_TO_SAVE, filename);

        createParentFileIfNecessary(path);

        Try(() -> java.nio.file.Files.write(path, lines, CREATE, APPEND));

        log.debug("writeLines size: {}", lines.size());
    }

    public static void write(String filename, byte[] content) {
        final var path = Paths.get(PATH_TO_SAVE, filename);

        createParentFileIfNecessary(path);

        Try(() -> java.nio.file.Files.write(path, content, WRITE));

        log.debug(" content has been written to {}", filename);
    }

    public static void createParentFileIfNecessary(String filename) {
        if (filename.contains(File.separator))
            createParentFileIfNecessary(Paths.get(filename));
    }

    private static void createParentFileIfNecessary(Path path) {
        if (path.getParent() == null) {
            return;
        }
        for (var parentFile = path.toFile().getParentFile(); parentFile != null && parentFile.mkdirs(); ) {
            parentFile = parentFile.getParentFile();
        }
    }

    public static void writeLinesIfNotExists(String filename, Collection<String> lines) {
        final var path = Paths.get(PATH_TO_SAVE, filename);

        if (notExists(path))
            Try(() -> java.nio.file.Files.write(path, lines, CREATE, APPEND));
    }

    public static List<String> readLines(String filename) {
        final var path = Paths.get(PATH_TO_SAVE, filename);

        if (notExists(path)) {
            return Collections.emptyList();
        }
        return Try(() -> java.nio.file.Files.readAllLines(path));
    }

    public static Stream<String> lines(String filename) {
        final var path = Paths.get(PATH_TO_SAVE, filename);

        if (notExists(path)) {
            return Stream.empty();
        }
        return Try(() -> java.nio.file.Files.lines(path));
    }

    public static void appendLine(String filename, String line) {

        appendLines(filename, Collections.singletonList(line));
    }

    public static Optional<String> lastLine(String filename) {
        return Optional.of(filename)
                .map(name -> Paths.get(PATH_TO_SAVE, name))
                .filter(Files::exists)
                .map(path -> Try(() -> java.nio.file.Files.readAllLines(path)))
                .filter(Predicate.not(List::isEmpty))
                .map(lines -> lines.get(lines.size() - 1));
    }

    private static boolean exists(Path path) {
        return java.nio.file.Files.exists(path);
    }

    private static boolean notExists(Path path) {
        return !exists(path);
    }
}
