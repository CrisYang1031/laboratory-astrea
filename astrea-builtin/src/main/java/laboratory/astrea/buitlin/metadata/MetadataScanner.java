package laboratory.astrea.buitlin.metadata;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.util.function.UnaryOperator;

import static laboratory.astrea.buitlin.core.Functions.Try;
import static laboratory.astrea.buitlin.core.KCollection.listOf;

public final class MetadataScanner {

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";
    private static final UnaryOperator<String> SEARCH_PATTERN = package_ -> CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(package_) + "/" + DEFAULT_RESOURCE_PATTERN;
    private static final ResourcePatternResolver PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new SimpleMetadataReaderFactory();


    private MetadataScanner() {
        throw new UnsupportedOperationException();
    }

    public static AnnotationMetadata forClassName(String className) {
        return Try(() -> METADATA_READER_FACTORY.getMetadataReader(className).getAnnotationMetadata());
    }

    public static Iterable<AnnotationMetadata> forBasePackage(String basePackage) {
        return listOf(Try(() -> PATTERN_RESOLVER.getResources(SEARCH_PATTERN.apply(basePackage))))
                .collect(resource -> Try(() -> METADATA_READER_FACTORY.getMetadataReader(resource)))
                .collect(MetadataReader::getAnnotationMetadata);
    }
}
