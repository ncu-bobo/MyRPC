package wfb.test.server;

import wfb.rpc.api.HelloService;
import wfb.rpc.core.netty.server.NettyServer;
import wfb.rpc.core.registry.DefaultServiceRegistry;
import wfb.rpc.core.registry.ServiceRegistry;

public class NettyTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        // 注册服务
        registry.register(helloService);
        // 启动服务
        NettyServer server = new NettyServer();
        server.start(9999);
    }

}
