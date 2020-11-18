package laboratory.astrea.redis;

import java.time.LocalDate;

public final class FreeTest {

    public static void main(String[] args) {

        final var personWrapped = new Wrapped<Person>();

        final var person = new Person();
        person.setAge(2);
        person.setCreatedAt(LocalDate.now());
        person.setName("TensorFlow");

        personWrapped.setData(person);
        personWrapped.setResult(true);

    }
}
