package com.drrr.domain.redis.util;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

@Component
public class ObjectBytesConverter {
    public <T> byte[] toBytes(T object) {
        try {
            return SerializationUtils.serialize(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object toObject(byte[] bytes) {
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes);
             ObjectInputStream o = new ObjectInputStream(b)) {
            return o.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
