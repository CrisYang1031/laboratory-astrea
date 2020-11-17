package laboratory.astrea.buitlin.instrument;

import javassist.CtClass;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@Slf4j
public final class JavassistInstrument implements ClassInstrument {

    private final Set<CtClass> usedClass = new HashSet<>();

    protected final CtClass mainClass;

    JavassistInstrument(String className, Function<String, CtClass> classProvider) {
        final var ctClass = classProvider.apply(className);
        usedClass.add(ctClass);
        this.mainClass = ctClass;
    }


    public JavassistInstrument addInterfaces(String... interfaceNames) {

        final var classes = Javassist.getClasses(interfaceNames);
        classes.forEach(usedClass::add);

        final var interfaces = classes.filter(CtClass::isInterface);

        Javassist.setInterface(mainClass, interfaces);

        return this;
    }

    @Override
    public ClassInstrument setSuperClass(String superClassName) {

        final var superClass = Javassist.getClass(superClassName);
        usedClass.add(superClass);

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
    public void cleanup() {
        usedClass.forEach(CtClass::detach);
        usedClass.clear();
    }

    @Override
    public Class<?> toClass() {
        return Javassist.toClass(mainClass);
    }

    @Override
    public byte[] bytecode() {
        return Javassist.toBytecode(mainClass);
    }


}
