package laboratory.astrea.buitlin.instrument;

public interface ClassInstrument {


    ClassInstrument addInterfaces(String... interfaceNames);


    ClassInstrument setSuperClass(String superClassName);


    ClassInstrument addField(String source);


    ClassInstrument addMethod(String source);


    void cleanup();


    Class<?> toClass();


    byte[] bytecode();

}
