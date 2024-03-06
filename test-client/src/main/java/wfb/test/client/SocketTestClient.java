package wfb.test.client;

import wfb.rpc.api.HelloObject;
import wfb.rpc.api.HelloService;
import wfb.rpc.core.RpcClientProxy;
import wfb.rpc.core.socket.client.SocketClient;

public class SocketTestClient {

    public static void main(String[] args) {
        SocketClient client = new SocketClient("127.0.0.1", 9000);
        RpcClientProxy proxy = new RpcClientProxy(client);
        // 生成代理对象HelloService类
        HelloService helloService = proxy.getProxy(HelloService.class);
        // 封装好一个请求对象
        HelloObject object = new HelloObject(101, "请求计算", 4,5);
        // 调用代理对象的hello方法()，实际调用的是代理类的invoke()方法
        String res = helloService.hello(object);
        System.out.println(res);
    }

}
