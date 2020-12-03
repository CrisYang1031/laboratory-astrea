package laboratory.astrea.redis;

import laboratory.astrea.buitlin.instrument.Javassist;
import laboratory.astrea.test.model.Wrapped;
import laboratory.astrea.redis.api.RScoped;

import java.util.List;

import static javassist.bytecode.SignatureAttribute.*;

public final class GenericTypeInstrumentTest {

    public static void main(String[] args) throws Exception {

        final var ctClass = Javassist.makeClass("laboratory.astrea.redis.WrappedInstrument$Javassist");
        ctClass.setSuperclass(Javassist.getClass(Wrapped.class.getName()));

        Javassist.setInterface(ctClass, List.of(Javassist.getClass(RScoped.class.getName())));

        final var classType = new ClassType(Wrapped.class.getName(), Javassist.GENERIC_TYPE_ARGUMENTS);

        final var interfaceType = new ClassType(RScoped.class.getName());

        final var classSignature = new ClassSignature(Javassist.GENERIC_TYPE_PARAMETERS, classType, new ClassType[]{interfaceType});

        ctClass.setGenericSignature(classSignature.encode());

        final Class<?> aClass = ctClass.toClass();

        System.out.println(aClass.toGenericString());
    }
}
