package laboratory.astrea.buitlin.instrument;

import javassist.CtClass;
import javassist.bytecode.SignatureAttribute.ClassType;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static laboratory.astrea.buitlin.core.KCollection.listOf;

@Slf4j
public final class JavassistInstrument implements ClassInstrument {

    private final static Map<CtClass, Set<Integer>> USED_CLASS_REFERENCE = new HashMap<>();

    private final static AtomicInteger SEQUENCE = new AtomicInteger();

    protected final CtClass mainClass;

    private final Integer identity = SEQUENCE.incrementAndGet() & Integer.MAX_VALUE;

    JavassistInstrument(String className, Function<String, CtClass> classProvider) {
        final var ctClass = classProvider.apply(className);
        attachCtClass(ctClass);
        this.mainClass = ctClass;

        //noinspection UnnecessaryLocalVariable
        final var _sequence = identity;

        CLASS_INSTRUMENT_CLEANER.register(this, () -> {
            try {
                cleanup(_sequence);
            } catch (Exception exception) {
                log.warn("something wrong in CLASS_INSTRUMENT_CLEANER", exception);
            }
        });
    }

    public JavassistInstrument setInterfaces(String... interfaceNames) {

        final var classes = Javassist.getClasses(interfaceNames);
        classes.forEach(this::attachCtClass);

        final var interfaces = classes.filter(CtClass::isInterface);

        Javassist.setInterface(mainClass, interfaces);

        return this;
    }

    @Override
    public ClassInstrument setSuperClass(String superClassName) {

        final var superClass = Javassist.getClass(superClassName);
        attachCtClass(superClass);

        Javassist.setSuperClass(mainClass, superClass);
        return this;
    }

    @Override
    public ClassInstrument setGenericType(String genericType, InstrumentGenericType superGenericType, Iterable<InstrumentGenericType> interfaceGenericTypes) {

        final var typeParameters = Javassist.genericTypeParameters(genericType);

        if (superGenericType.isNothing() && !interfaceGenericTypes.iterator().hasNext()) {

            Javassist.setGenericSignature(mainClass, typeParameters);

        } else {

            final var superClass = Javassist.genericClassType(superGenericType);

            final var interfaces = listOf(interfaceGenericTypes)
                    .collect(Javassist::genericClassType)
                    .stream()
                    .toArray(ClassType[]::new);

            Javassist.setGenericSignature(mainClass, typeParameters, superClass, interfaces);
        }

        return this;
    }


    @Override
    public ClassInstrument addField(String source) {
        Javassist.addField(mainClass, source);
        return this;
    }

    @Override
    public ClassInstrument addMethod(String source) {
        Javassist.addMethod(mainClass, source);
        return this;
    }

    @Override
    public Class<?> toClass() {
        return Javassist.toClass(mainClass);
    }

    @Override
    public byte[] bytecode() {
        return Javassist.toBytecode(mainClass);
    }


    private void attachCtClass(CtClass ctClass) {
        synchronized (USED_CLASS_REFERENCE) {
            final var identitySet = USED_CLASS_REFERENCE.computeIfAbsent(ctClass, __ -> new HashSet<>());
            identitySet.add(identity);
        }
    }

    private static void cleanup(Integer identity) {
        synchronized (USED_CLASS_REFERENCE) {
            USED_CLASS_REFERENCE.forEach((ctClass, set) -> set.remove(identity));

            final var toDetachClass = USED_CLASS_REFERENCE.entrySet().stream()
                    .filter(entry -> entry.getValue().isEmpty())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            toDetachClass.forEach(ctClass -> {
                USED_CLASS_REFERENCE.remove(ctClass);
                Javassist.detach(ctClass);
            });
        }
    }
}
