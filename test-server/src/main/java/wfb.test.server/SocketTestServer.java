package wfb.test.server;

import wfb.rpc.core.RpcServer;
import wfb.rpc.core.annotation.ServiceScan;
import wfb.rpc.core.net.socket.server.SocketServer;
import wfb.rpc.core.serializer.CommonSerializer;

@ServiceScan
public class SocketTestServer {

    public static void main(String[] args) {
        RpcServer server = new SocketServer("127.0.0.1", 9998, CommonSerializer.HESSIAN_SERIALIZER);
        server.start();
    }


}
