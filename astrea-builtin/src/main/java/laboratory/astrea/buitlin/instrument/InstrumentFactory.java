package laboratory.astrea.buitlin.instrument;

public enum InstrumentFactory {

    Javassist_ {
        @Override
        public ClassInstrument create(String className) {
            return new JavassistInstrument(className, Javassist::makeClass);
        }
    },

    ;


    public abstract ClassInstrument create(String className);
}
