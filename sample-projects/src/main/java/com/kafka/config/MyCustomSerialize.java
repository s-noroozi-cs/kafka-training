package com.kafka.config;

import com.exception.LogModelSerializeException;
import com.model.LogModel;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class MyCustomSerialize implements Serializer<LogModel> {
    @Override
    public byte[] serialize(String s, LogModel logModel) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(logModel);
            return baos.toByteArray();
        }catch (Throwable ex){
            throw new LogModelSerializeException(ex.getMessage(),ex);
        }
    }
}
