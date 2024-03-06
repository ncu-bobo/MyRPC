package wfb.test.server;

import wfb.rpc.api.HelloService;
import wfb.rpc.core.registry.DefaultServiceRegistry;
import wfb.rpc.core.registry.ServiceRegistry;
import wfb.rpc.core.socket.server.SocketServer;

public class SocketTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        // 注册HelloService服务
        serviceRegistry.register(helloService);
        SocketServer rpcServer = new SocketServer(serviceRegistry);
        rpcServer.start(9000);
    }


}
