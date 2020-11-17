package laboratory.astrea.redis.api;

public interface RScoped {


    void commit();


    void associate(Object rObject);
}
