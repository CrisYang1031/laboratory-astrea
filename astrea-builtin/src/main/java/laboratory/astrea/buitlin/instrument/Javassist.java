package laboratory.astrea.buitlin.instrument;

import io.vavr.CheckedFunction0;
import io.vavr.collection.List;
import javassist.*;
import lombok.extern.slf4j.Slf4j;

import static laboratory.astrea.buitlin.core.Functions.Try;
import static laboratory.astrea.buitlin.core.KCollection.array;
import static laboratory.astrea.buitlin.core.TopLevelFunctions.sneakThrow;

@Slf4j
class Javassist {

    private static final ClassPool CLASS_POOL = new ClassPool();

    static {
        CLASS_POOL.appendSystemPath();
        CtClass.debugDump = "./debug";
    }

    public static CtClass makeClass(String className) {
        return CLASS_POOL.makeClass(className);
    }


    public static CtClass getClass(String className) {
        return Try(() -> CLASS_POOL.getCtClass(className));
    }

    public static List<CtClass> getClasses(String... className) {
        return List.of(className).map(Javassist::getClass);
    }

    public static void setInterface(CtClass ctClass, Iterable<CtClass> interfaces) {
        ctClass.setInterfaces(array(interfaces, CtClass[]::new));
    }

    public static void addField(CtClass ctClass, String src) {
        try {
            ctClass.addField(makeField(ctClass, src));
        } catch (CannotCompileException e) {
            log.error("addField failure", e);
            sneakThrow(e);
        }
    }

    public static void addMethod(CtClass ctClass, String src) {
        try {
            ctClass.addMethod(makeMethod(ctClass, src));
        } catch (CannotCompileException e) {
            log.error("addMethod failure", e);
            sneakThrow(e);
        }
    }

    public static CtField makeField(CtClass ctClass, String src) {
        return Try(() -> CtField.make(src, ctClass));
    }

    public static CtMethod makeMethod(CtClass ctClass, String src) {
        return Try(() -> CtMethod.make(src, ctClass));
    }

    public static Class<?> toClass(CtClass ctClass) {
        return Try((CheckedFunction0<? extends Class<?>>) ctClass::toClass);
    }
}
