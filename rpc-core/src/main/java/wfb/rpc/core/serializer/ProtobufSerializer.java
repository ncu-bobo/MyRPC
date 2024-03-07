package wfb.rpc.core.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import wfb.rpc.common.enumeration.SerializerCode;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用ProtoBuf的序列化器
 */
public class ProtobufSerializer implements CommonSerializer {

    private LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    // 建立一个schema缓存
    private Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    /**
     * 序列化对象到字节数组。
     * @param obj 要序列化的对象。
     * @return 序列化后的字节数组。
     */
    public byte[] serialize(Object obj) {
        Class clazz = obj.getClass();
        Schema schema = getSchema(clazz);
        byte[] data;
        try {
            // 序列化时需要存入该类的schema模板
            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
        return data;
    }

    /**
     * 从字节数组反序列化对象。
     * @param bytes 字节数组。
     * @param clazz 目标对象的类。
     * @return 反序列化的对象。
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        // 获取schema模板
        Schema schema = getSchema(clazz);
        // 根据schema给出的模板创建一个新的空对象实例
        Object obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    /**
     * 获取序列化器的代码。
     * @return 序列化器代码。
     */
    @Override
    public int getCode() {
        return SerializerCode.valueOf("PROTOBUF").getCode();
    }

    //提供一种高效的方式来获取类的Schema对象，通过使用缓存机制来避免重复的计算成本。
    @SuppressWarnings("unchecked")
    private Schema getSchema(Class clazz) {
        // 它首先尝试从缓存中获取Schema，
        Schema schema = schemaCache.get(clazz);
        // 如果缓存中不存在，则创建一个新的Schema，然后将其存储在缓存中以供将来使用。
        if (Objects.isNull(schema)) {
            // 这个schema通过RuntimeSchema进行懒创建并缓存
            // 所以可以一直调用RuntimeSchema.getSchema(),这个方法是线程安全的
            schema = RuntimeSchema.getSchema(clazz);
            if (Objects.nonNull(schema)) {
                schemaCache.put(clazz, schema);
            }
        }
        return schema;
    }


}
