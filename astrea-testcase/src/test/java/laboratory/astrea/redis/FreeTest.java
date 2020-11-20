package laboratory.astrea.redis;

import laboratory.astrea.buitlin.core.Parameterized;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

public final class FreeTest {

    public static void main(String[] args) {

        final ParameterizedTypeReference<List<Person>> typeReference = new ParameterizedTypeReference<>() {
        };

        final var synthesizeGeneric = Parameterized.synthesizeGeneric(Wrapped.class, typeReference);

        System.out.println(synthesizeGeneric);


    }
}
