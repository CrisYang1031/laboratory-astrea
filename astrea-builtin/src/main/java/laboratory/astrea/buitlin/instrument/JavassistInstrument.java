package laboratory.astrea.buitlin.instrument;

import javassist.CtClass;
import javassist.bytecode.SignatureAttribute.ClassType;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static laboratory.astrea.buitlin.core.KCollection.listOf;

@Slf4j
public final class JavassistInstrument implements ClassInstrument {

    private final static Set<CtClass> USED_CLASS = new HashSet<>();

    protected final CtClass mainClass;


    JavassistInstrument(String className, Function<String, CtClass> classProvider) {
        final var ctClass = classProvider.apply(className);
        attachCtClass(ctClass);
        this.mainClass = ctClass;

        CLASS_INSTRUMENT_CLEANER.register(this, JavassistInstrument::cleanup);
    }

    public JavassistInstrument setInterfaces(String... interfaceNames) {

        final var classes = Javassist.getClasses(interfaceNames);
        classes.forEach(USED_CLASS::add);

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
        synchronized (USED_CLASS) {
            USED_CLASS.add(ctClass);
        }
    }

    private static void cleanup() {
        synchronized (USED_CLASS) {
            USED_CLASS.forEach(CtClass::detach);
            USED_CLASS.clear();
        }
    }
}
