package laboratory.astrea.buitlin.core;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.*;
import static laboratory.astrea.buitlin.core.Functions.Try;

@Slf4j
public final class Files {

    private static final String PATH_TO_SAVE;

    private static final String FILE_SEPARATOR =  FileSystems.getDefault().getSeparator();

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

    public static void writeLines(String filename, Collection<String> lines) {
        final var path = Paths.get(PATH_TO_SAVE, filename);
        Try(() -> java.nio.file.Files.write(path, lines, CREATE, WRITE));
    }

    public static void writeLinesIfNotExists(String filename, Collection<String> lines) {
        final var path = Paths.get(PATH_TO_SAVE, filename);

        if (notExists(path))
            Try(() -> java.nio.file.Files.write(path, lines, CREATE, APPEND));
    }

    public static void writeLinesIfNotExists(Path path, Collection<String> lines) {

        createParentFileIfNecessary(path);

        if (notExists(path))
            Try(() -> java.nio.file.Files.write(path, lines, CREATE, APPEND));
    }

    public static void writeStringIfNotExists(Path path, String content) {

        createParentFileIfNecessary(path);

        if (notExists(path))
            Try(() -> java.nio.file.Files.writeString(path, content, CREATE, WRITE));
    }


    public static List<String> readLines(String filename) {
        final var path = Paths.get(PATH_TO_SAVE, filename);

        return readLines(path);
    }

    public static List<String> readLines(Path path){
        if (notExists(path)) {
            return Collections.emptyList();
        }
        return Try(() -> java.nio.file.Files.readAllLines(path));
    }


    public static String readString(Path path){
        if (notExists(path)) {
            return "";
        }
        return Try(() -> java.nio.file.Files.readString(path));
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


    public static Stream<Path> findPath(String parentPath) {
        return findPath(Paths.get(parentPath));
    }

    public static Stream<Path> findPath(Path parentPath) {
        return findPath(parentPath, 1, (path, basicFileAttributes) -> true);
    }

    public static Stream<Path> findAllPath(String parentPath) {
        return findAllPath(Paths.get(parentPath));
    }

    public static Stream<Path> findAllPath(Path parentPath) {
        return findPath(parentPath, 99, (path, basicFileAttributes) -> true);
    }

    public static Stream<Path> findPath(Path parentPath, int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher) {
        return Try(() -> java.nio.file.Files.find(parentPath, maxDepth, matcher)).skip(1);
    }

    public static boolean isDirectory(Path path) {
        return java.nio.file.Files.isDirectory(path);
    }

    public static boolean isRegularFile(Path path) {
        return java.nio.file.Files.isRegularFile(path);
    }

    public static String relativePackageName(Path ancestorPath, Path path) {
        if (!isDirectory(ancestorPath)) {
            throw new IllegalArgumentException("ancestorPath must be a directory");
        }
        var list = io.vavr.collection.List.<String>empty();
        var parent = path.getParent();
        do {
            list = list.prepend(parent.getFileName().toString());
            parent = parent.getParent();
            if (parent == null){
                throw new IllegalArgumentException(String.format("can not find relative package %s in %s", path, ancestorPath));
            }
        } while (!ancestorPath.equals(parent));

        return list.mkString(".");
    }

    public static Path parsePackage(String packageName){
        return Paths.get(packageName.replaceAll("\\.", FILE_SEPARATOR));
    }

    public static Path synthesize(Path parent, Path path){
        return Paths.get(parent.toString() + FILE_SEPARATOR + path.toString());
    }


    private static boolean exists(Path path) {
        return java.nio.file.Files.exists(path);
    }


    private static boolean notExists(Path path) {
        return !exists(path);
    }
}
