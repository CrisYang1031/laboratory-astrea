package laboratory.astrea.buitlin.instrument;

import io.vavr.CheckedFunction0;
import io.vavr.collection.List;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.bytecode.SignatureAttribute.ClassSignature;
import javassist.bytecode.SignatureAttribute.TypeParameter;
import lombok.extern.slf4j.Slf4j;

import static javassist.bytecode.SignatureAttribute.*;
import static laboratory.astrea.buitlin.core.Functions.Try;
import static laboratory.astrea.buitlin.core.KCollection.array;

@Slf4j
public class Javassist {

    private static final ClassPool CLASS_POOL = new ClassPool();

    public static final String TYPE_SYMBOL = "T";

    public static final TypeVariable TYPE_VARIABLE_T = new TypeVariable(TYPE_SYMBOL);

    public static final TypeParameter GENERIC_TYPE_PARAMETER = new TypeParameter(TYPE_SYMBOL);

    public static final TypeArgument GENERIC_TYPE_ARGUMENT = new TypeArgument(TYPE_VARIABLE_T);

    public static final TypeParameter[] GENERIC_TYPE_PARAMETERS = new TypeParameter[]{GENERIC_TYPE_PARAMETER};

    public static final TypeParameter[] EMPTY_TYPE_PARAMETERS = new TypeParameter[]{};

    public static final TypeArgument[] GENERIC_TYPE_ARGUMENTS = new TypeArgument[]{GENERIC_TYPE_ARGUMENT};

    public static final TypeArgument[] EMPTY_TYPE_ARGUMENTS = new TypeArgument[]{};


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

    public static void setName(CtClass ctClass, String name){
        ctClass.setName(name);
    }

    public static void setInterface(CtClass ctClass, Iterable<CtClass> interfaces) {
        ctClass.setInterfaces(array(interfaces, CtClass[]::new));
    }

    public static void setSuperClass(CtClass ctClass, CtClass superClass) {
        Try(() -> ctClass.setSuperclass(superClass));
    }

    public static void setGenericSignature(CtClass ctClass, ClassSignature classSignature) {
        ctClass.setGenericSignature(classSignature.encode());
    }

    public static void setGenericSignature(CtClass ctClass, TypeParameter[] typeParameters) {
        ctClass.setGenericSignature(new ClassSignature(typeParameters).encode());
    }

    public static void setGenericSignature(CtClass ctClass, TypeParameter[] typeParameters, ClassType superClass, ClassType[] interfaces) {
        ctClass.setGenericSignature(new ClassSignature(typeParameters, superClass, interfaces).encode());
    }

    public static TypeParameter genericTypeParameter(String genericType) {
        if (TYPE_SYMBOL.equals(genericType)) return GENERIC_TYPE_PARAMETER;
        else return new TypeParameter(genericType);
    }


    public static TypeParameter[] genericTypeParameters(String genericType) {
        if (TYPE_SYMBOL.equals(genericType)) return GENERIC_TYPE_PARAMETERS;
        return new TypeParameter[]{genericTypeParameter(genericType)};
    }

    public static TypeArgument genericTypeArgument(String genericType) {
        if (TYPE_SYMBOL.equals(genericType)) return GENERIC_TYPE_ARGUMENT;
        else return new TypeArgument(new TypeVariable(genericType));
    }

    public static ClassSignature makeClassSignature(TypeParameter[] typeParameters) {
        return new ClassSignature(typeParameters);
    }

    public static ClassType genericClassType(InstrumentGenericType genericType) {

        if (genericType.getGenericTypes().isEmpty()) return new ClassType(genericType.getClassName());

        final var typeParameters = genericType.getGenericTypes()
                .map(Javassist::genericTypeArgument)
                .toJavaArray(TypeArgument[]::new);

        return new ClassType(genericType.getClassName(), typeParameters);
    }

    public static void addField(CtClass ctClass, String src) {
        Try(() -> ctClass.addField(makeField(ctClass, src)));
    }


    public static void addMethod(CtClass ctClass, String src) {
        Try(() -> ctClass.addMethod(makeMethod(ctClass, src)));
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

    public static byte[] toBytecode(CtClass ctClass) {
        return Try((CheckedFunction0<byte[]>) ctClass::toBytecode);
    }

    public static void detach(CtClass ctClass){
        ctClass.detach();
        System.out.println(ctClass.getName() + " has been detached");
    }

}
