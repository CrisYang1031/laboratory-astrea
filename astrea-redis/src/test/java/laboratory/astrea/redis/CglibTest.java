package laboratory.astrea.redis;


import org.springframework.asm.Type;
import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.core.DefaultGeneratorStrategy;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.transform.TransformingClassGenerator;
import org.springframework.cglib.transform.impl.AddPropertyTransformer;

public final class CglibTest {

    public static void main(String[] args) {

        Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(Person.class);

        enhancer.setStrategy(new DefaultGeneratorStrategy() {
            @Override
            protected ClassGenerator transform(ClassGenerator cg) {
                return new TransformingClassGenerator(cg, new AddPropertyTransformer(new String[]{"foo"}, new Type[]{Type.getType(Integer.TYPE)}));
            }
        });



        final var aClass = enhancer.createClass();

    }
}

