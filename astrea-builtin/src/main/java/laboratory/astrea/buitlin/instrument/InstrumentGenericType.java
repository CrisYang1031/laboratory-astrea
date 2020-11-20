package laboratory.astrea.buitlin.instrument;

import io.vavr.collection.List;
import lombok.Getter;


@Getter
public class InstrumentGenericType {

    private final String className;

    private final List<String> genericTypes;

    private InstrumentGenericType(String className, List<String> genericTypes) {
        this.className = className;
        this.genericTypes = genericTypes;
    }

    public static final InstrumentGenericType Nothing = new InstrumentGenericType(null, List.empty());

    public static InstrumentGenericType of(String className) {
        return of(className, "T");
    }

    public static InstrumentGenericType nonGeneric(String className) {
        return new InstrumentGenericType(className, List.empty());
    }

    public static InstrumentGenericType of(String className, String genericType) {
        return new InstrumentGenericType(className, List.of(genericType));
    }

    public static InstrumentGenericType of(String className, String... genericType) {
        return new InstrumentGenericType(className, List.of(genericType));
    }

    public boolean isNothing(){
        return Nothing == this;
    }

}
