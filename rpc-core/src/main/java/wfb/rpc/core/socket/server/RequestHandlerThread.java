package wfb.rpc.core.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wfb.rpc.common.entity.RpcRequest;
import wfb.rpc.common.entity.RpcResponse;
import wfb.rpc.core.registry.ServiceRegistry;
import wfb.rpc.core.RequestHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//处理RpcRequest的工作线程
public class RequestHandlerThread implements Runnable {

    // 定义日志记录器
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    // 客户端套接字连接
    private Socket socket;
    // 请求处理器
    private RequestHandler requestHandler;
    // 服务注册表
    private ServiceRegistry serviceRegistry;

    // 构造函数，初始化套接字、请求处理器和服务注册表
    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        try (
            // 创建对象输入流，用于接收客户端发送的对象
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // 创建对象输出流，用于向客户端发送对象
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            // 读取客户端请求对象
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            // 获取请求的接口名称
            String interfaceName = rpcRequest.getInterfaceName();
            // 从服务注册表中获取服务实例
            Object service = serviceRegistry.getService(interfaceName);
            // 处理请求，并获取结果
            Object result = requestHandler.handle(rpcRequest, service);
            // 向客户端发送响应结果
            objectOutputStream.writeObject(RpcResponse.success(result));
            // 刷新输出流，确保数据发送
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            // 捕获到异常时，记录错误信息
            logger.error("调用或发送时有错误发生：", e);
        }
    }

}

