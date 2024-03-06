package wfb.test.client;

import wfb.rpc.api.HelloObject;
import wfb.rpc.api.HelloService;
import wfb.rpc.core.client.RpcClientProxy;

public class TestClient {

    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        // 生成代理对象HelloService类
        HelloService helloService = proxy.getProxy(HelloService.class);
        // 封装好一个请求对象
        HelloObject object = new HelloObject(101, "请求计算", 4,5);
        // 调用代理对象的hello方法()，实际调用的是代理类的invoke()方法
        String res = helloService.hello(object);
        System.out.println(res);
    }

}
