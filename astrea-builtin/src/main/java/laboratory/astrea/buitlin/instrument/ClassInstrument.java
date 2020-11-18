package laboratory.astrea.buitlin.instrument;

import java.lang.ref.Cleaner;

public interface ClassInstrument {


    ClassInstrument addInterfaces(String... interfaceNames);


    ClassInstrument setSuperClass(String superClassName);


    ClassInstrument addField(String source);


    ClassInstrument addMethod(String source);


    Class<?> toClass();


    byte[] bytecode();


    Cleaner CLASS_INSTRUMENT_CLEANER = Cleaner.create();
}
