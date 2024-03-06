package wfb.test.server;

import wfb.rpc.api.HelloService;
import wfb.rpc.core.registry.DefaultServiceRegistry;
import wfb.rpc.core.registry.ServiceRegistry;
import wfb.rpc.core.server.RpcServer;

public class TestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        // 注册HelloService服务
        serviceRegistry.register(helloService);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        rpcServer.start(9000);
    }


}
