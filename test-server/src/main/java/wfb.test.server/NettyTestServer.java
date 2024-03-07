package wfb.test.server;

import wfb.rpc.api.HelloService;
import wfb.rpc.core.RpcServer;
import wfb.rpc.core.annotation.ServiceScan;
import wfb.rpc.core.net.netty.server.NettyServer;
import wfb.rpc.core.serializer.CommonSerializer;

@ServiceScan
public class NettyTestServer {

    public static void main(String[] args) {
        // 选用protobuf作为序列化方案
        RpcServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();
    }

}
