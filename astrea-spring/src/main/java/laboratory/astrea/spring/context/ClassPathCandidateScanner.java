package laboratory.astrea.spring.context;

import io.vavr.collection.List;
import laboratory.astrea.buitlin.metadata.MetadataScanner;
import lombok.AccessLevel;
import lombok.With;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

import static laboratory.astrea.buitlin.core.Functions.*;


public final class ClassPathCandidateScanner {

    private static final ClassPathCandidateScanner Companion = new ClassPathCandidateScanner(alwaysTrue());

    @With(AccessLevel.PRIVATE)
    private final Predicate<AnnotationMetadata> metadataPredicate;


    private ClassPathCandidateScanner(Predicate<AnnotationMetadata> metadataPredicate) {
        this.metadataPredicate = metadataPredicate;
    }

    public Iterable<AnnotationMetadata> scan(String basePackage) {
        return List.ofAll(MetadataScanner.forBasePackage(basePackage))
                .filter(metadataPredicate);
    }

    public static ClassPathCandidateScanner annotated(Class<? extends Annotation> annotationClass) {
        return Companion.withMetadataPredicate(annotationMetadata -> annotationMetadata.hasAnnotation(annotationClass.getName()));
    }

    public static ClassPathCandidateScanner annotated(String annotationName) {
        return Companion.withMetadataPredicate(annotationMetadata -> annotationMetadata.hasAnnotation(annotationName));
    }

    public static ClassPathCandidateScanner concreteOf(Class<?> className) {
        return concreteOf(className.getName());
    }

    public static ClassPathCandidateScanner concreteOf(String className) {
        //noinspection ConstantConditions
        return Companion.withMetadataPredicate(it -> it.hasSuperClass() && it.getSuperClassName().equals(className));
    }

    public static ClassPathCandidateScanner instanceOf(Class<?> interfaceClass) {
        return instanceOf(interfaceClass.getName());
    }

    public static ClassPathCandidateScanner instanceOf(String interfaceName) {
        return Companion.withMetadataPredicate(it -> List.of(it.getInterfaceNames()).find(name -> name.equals(interfaceName)).isDefined());
    }

    public ClassPathCandidateScanner isInterface() {
        return this.withMetadataPredicate(this.metadataPredicate.and(ClassMetadata::isInterface));
    }

    public ClassPathCandidateScanner isAbstract() {
        return this.withMetadataPredicate(this.metadataPredicate.and(ClassMetadata::isAbstract));
    }

    public ClassPathCandidateScanner isIndependent() {
        return this.withMetadataPredicate(this.metadataPredicate.and(ClassMetadata::isIndependent));
    }


}
