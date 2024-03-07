package wfb.test.client;

import wfb.rpc.api.ByeService;
import wfb.rpc.api.HelloObject;
import wfb.rpc.api.HelloService;
import wfb.rpc.core.RpcClientProxy;
import wfb.rpc.core.net.socket.client.SocketClient;
import wfb.rpc.core.serializer.CommonSerializer;

public class SocketTestClient {

    public static void main(String[] args) {
        SocketClient client = new SocketClient(CommonSerializer.KRYO_SERIALIZER);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(101, "请求计算", 34, 32);
        String res = helloService.hello(object);
        System.out.println(res);
        ByeService byeService = proxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("Netty"));
    }

}
