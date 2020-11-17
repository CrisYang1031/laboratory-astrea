package laboratory.astrea.buitlin;

import laboratory.astrea.buitlin.core.KCollection;

import java.io.Serializable;
import java.util.List;

public final class FreeTest {


    public static void main(String[] args) {

        final Class<?>[] classes = KCollection.array(List.of(Integer.class, String.class), Class[]::new);

        for (Class<?> aClass : classes) {

            System.out.println(aClass.getName());
        }

    }
}
