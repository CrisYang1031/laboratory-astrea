package laboratory.astrea.test.model;

import lombok.Data;

@Data
public class Wrapped<T> {

    private T data;

    private boolean result;
}
