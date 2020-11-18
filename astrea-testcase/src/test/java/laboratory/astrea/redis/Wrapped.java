package laboratory.astrea.redis;

import lombok.Data;

@Data
public class Wrapped<T> {

    private T data;

    private boolean result;
}
