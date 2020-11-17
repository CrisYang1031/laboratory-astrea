package laboratory.astrea.redis;

import laboratory.astrea.redis.operation.LockOperation;
import laboratory.astrea.redis.operation.PubSubOperation;
import laboratory.astrea.redis.operation.ValueOperation;
import laboratory.astrea.redis.operation.ValueOperationExtension;

public interface RedisSyncOperationFactory {


    ValueOperation valueOperation();



    LockOperation lockOperation();



    PubSubOperation pubSubOperation();



    ValueOperationExtension valueExtension();


    //----------------------   static   ----------------------

    static RedisSyncOperationFactory create(SyncConnectionContext connectionContext){
        return new RedisSyncOperationFactoryImpl(connectionContext);
    }


}
