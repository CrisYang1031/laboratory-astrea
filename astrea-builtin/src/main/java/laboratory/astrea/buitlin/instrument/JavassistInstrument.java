package laboratory.astrea.buitlin.instrument;

import javassist.CtClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;

@Slf4j
public final class JavassistInstrument implements ClassInstrument {

    private final static Set<CtClass> USED_CLASS = new ConcurrentSkipListSet<>(Comparator.comparing(Object::hashCode));

    protected final CtClass mainClass;


    JavassistInstrument(String className, Function<String, CtClass> classProvider) {
        final var ctClass = classProvider.apply(className);
        USED_CLASS.add(ctClass);
        this.mainClass = ctClass;

        CLASS_INSTRUMENT_CLEANER.register(this, JavassistInstrument::cleanup);
    }


    public JavassistInstrument addInterfaces(String... interfaceNames) {

        final var classes = Javassist.getClasses(interfaceNames);
        classes.forEach(USED_CLASS::add);

        final var interfaces = classes.filter(CtClass::isInterface);

        Javassist.setInterface(mainClass, interfaces);

        return this;
    }

    @Override
    public ClassInstrument setSuperClass(String superClassName) {

        final var superClass = Javassist.getClass(superClassName);
        USED_CLASS.add(superClass);

        Javassist.setSuperClass(mainClass, superClass);
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


    private static void cleanup() {
        USED_CLASS.forEach(CtClass::detach);
        USED_CLASS.clear();
    }
}
