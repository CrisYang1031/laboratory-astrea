package laboratory.astrea.buitlin.instrument;

import javassist.CtClass;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@Slf4j
public final class JavassistInstrument implements ClassInstrument {

    private final Set<CtClass> usedClass = new HashSet<>();

    protected final CtClass ctClass;

    JavassistInstrument(String className, Function<String, CtClass> classProvider) {
        final var ctClass = classProvider.apply(className);
        usedClass.add(ctClass);
        this.ctClass = ctClass;
    }


    public JavassistInstrument addInterfaces(String... interfaceNames) {

        final var classes = Javassist.getClasses(interfaceNames);
        classes.forEach(usedClass::add);

        final var interfaces = classes.filter(CtClass::isInterface);

        Javassist.setInterface(ctClass, interfaces);

        return this;
    }

    @Override
    public ClassInstrument setSuperClass(String superClassName) {
        return null;
    }

    @Override
    public ClassInstrument addField(String source) {
        return null;
    }

    @Override
    public ClassInstrument addMethod(String source) {
        return null;
    }


    @Override
    public void cleanup() {
        usedClass.forEach(CtClass::detach);
        usedClass.clear();
    }

    @Override
    public Class<?> toClass() {
        return null;
    }

    @Override
    public byte[] byteCodes() {
        return new byte[0];
    }


}
