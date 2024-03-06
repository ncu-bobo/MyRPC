package wfb.rpc.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import wfb.rpc.common.entity.RpcRequest;
import wfb.rpc.common.enumeration.PackageType;
import wfb.rpc.core.serializer.CommonSerializer;

/**
 * 通用的编码拦截器
 */
public class CommonEncoder extends MessageToByteEncoder {

    // 定义好的一个指定数字
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 写入magic
        out.writeInt(MAGIC_NUMBER);
        // 写入包的类型
        if(msg instanceof RpcRequest) {
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        } else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        // 写入所使用的序列化器
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        // 写入数据长度
        out.writeInt(bytes.length);
        // 写入已序列化后的实际数据
        out.writeBytes(bytes);
    }

}
