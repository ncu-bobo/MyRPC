package wfb.test.client;

import wfb.rpc.api.HelloObject;
import wfb.rpc.api.HelloService;
import wfb.rpc.core.RpcClient;
import wfb.rpc.core.RpcClientProxy;
import wfb.rpc.core.netty.client.NettyClient;

public class NettyTestClient {

    public static void main(String[] args) {
        RpcClient client = new NettyClient("127.0.0.1", 9999);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(101, "请求计算",4,5);
        String res = helloService.hello(object);
        System.out.println(res);

    }

}