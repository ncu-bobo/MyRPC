package wfb.test.client;

import wfb.rpc.api.ByeService;
import wfb.rpc.api.HelloObject;
import wfb.rpc.api.HelloService;
import wfb.rpc.core.RpcClient;
import wfb.rpc.core.RpcClientProxy;
import wfb.rpc.core.net.netty.client.NettyClient;
import wfb.rpc.core.serializer.CommonSerializer;

public class NettyTestClient {

    public static void main(String[] args) {
        RpcClient client = new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(101, "请求计算", 34, 32);
        String res = helloService.hello(object);
        System.out.println(res);
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("Netty"));
    }

}