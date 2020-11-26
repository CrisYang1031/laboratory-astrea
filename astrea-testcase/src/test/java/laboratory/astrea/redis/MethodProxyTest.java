package laboratory.astrea.redis;

import javassist.CtNewMethod;

import static laboratory.astrea.buitlin.core.KCollection.listOf;

public final class MethodProxyTest {


    public static void main(String[] args) throws Exception {

//        CtNewMethod.make(, , )


//        final var ctClass = Javassist.makeClass("laboratory.astrea.redis.PersonService$Javassist");
//
//        final var aClass = Javassist.getClass("laboratory.astrea.redis.MethodProxyTest$PersonServiceProxy");
//
//        final var testArgument = aClass.getDeclaredMethod("testArgument");
//
//        final var ctMethod = CtNewMethod.copy(testArgument, ctClass, null);
//        ctClass.addMethod(ctMethod);
//
//        ctClass.toClass();

//        final var classInstrument = Javassist_.create("laboratory.astrea.redis.PersonService$Javassist");

//        listOf(PersonService.class.getMethods()).forEach(method -> {
//
//            classInstrument.addMethod(String.format("public %s getPersonByName(String personName) { " +
//                    " System.out.println($args);" +
//                    "return null;" +
//                    " }", Person.class.getName()));
//        });

//        classInstrument.addMethod("public void testArgument(String personName, String date) { " +
//                "             System.out.println(String.join(\",\", new CharSequence[] {$1, $2}));\n" +
//                " }");
//
//        final Class<?> aClass = classInstrument.toClass();

    }


    interface PersonService {

        Person getPersonByName(String personName);
    }

    static class PersonServiceImpl implements PersonService {

        @Override
        public Person getPersonByName(String personName) {
            final var person = new Person();
            person.setName(personName);
            return person;
        }
    }


    static class PersonServiceProxy implements PersonService {

        private final PersonService personService;

        PersonServiceProxy(PersonService personService) {
            this.personService = personService;
        }

        @Override
        public Person getPersonByName(String personName) {
            return personService.getPersonByName(personName);
        }

        public void testArgument(String personName, String date) {
            System.out.println(String.join(",",new CharSequence[] {personName, date}));
        }
    }

}
