package laboratory.astrea.redis.api;

import laboratory.astrea.redis.SyncConnectionContext;
import laboratory.astrea.redis.api.impl.RadianceImpl;
import org.springframework.core.ParameterizedTypeReference;

public interface Radiance {


    <T> RValue<T> getValue(String name, Class<T> typeReference);


    <T> RValue<T> getValue(String name, ParameterizedTypeReference<T> typeReference);


    void radianceScope(Runnable runnable);


    <T> T scopedValue(String name, Class<T> typeReference);


    <T> T scopedValue(String name, ParameterizedTypeReference<T> typeReference);




    //----------------------   static   ----------------------

    static Radiance create(SyncConnectionContext connectionContext){
        return new RadianceImpl(connectionContext);
    }
}
