package laboratory.astrea.buitlin.instrument;

import laboratory.astrea.buitlin.core.Pair;

import java.lang.ref.Cleaner;

public interface ClassInstrument {

    String TYPE_PARAMETER_SYMBOL = "T";


    ClassInstrument setInterfaces(String... interfaceNames);


    ClassInstrument setSuperClass(String superClassName);


    default ClassInstrument setGenericType(InstrumentGenericType superGenericType, Iterable<InstrumentGenericType> interfaceGenericTypes) {
        return setGenericType(TYPE_PARAMETER_SYMBOL, superGenericType, interfaceGenericTypes);
    }

    ClassInstrument setGenericType(String genericType, InstrumentGenericType superGenericType, Iterable<InstrumentGenericType> interfaceGenericTypes);


    ClassInstrument addField(String source);


    ClassInstrument addMethod(String source);


    Class<?> toClass();


    byte[] bytecode();


    Cleaner CLASS_INSTRUMENT_CLEANER = Cleaner.create();
}
