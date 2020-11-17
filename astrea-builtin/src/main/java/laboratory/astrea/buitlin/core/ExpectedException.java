package laboratory.astrea.buitlin.core;


import lombok.Getter;

@Getter
public class ExpectedException extends RuntimeException {

    private final String code;

    private final Object attachment;

    public ExpectedException(String code) {
        this(code, code, null);
    }

    public ExpectedException(String code, String message) {
        this(code, message, null);
    }

    public ExpectedException(String code, String message, Object attachment) {
        super(message);
        this.code = code;
        this.attachment = attachment;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
