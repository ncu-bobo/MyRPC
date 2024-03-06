package wfb.rpc.core.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wfb.rpc.common.entity.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler {

    // 定义日志记录器
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);
    // RPC服务的主机地址
    private String host;
    // RPC服务的端口号
    private int port;

    // 构造函数，初始化RPC服务的主机地址和端口号
    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // 生成代理对象
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        // 使用JDK动态代理生成代理对象
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 记录调用的方法信息
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        // 构建RPC请求对象
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName()) // 接口名
                .methodName(method.getName()) // 方法名
                .parameters(args) // 方法参数
                .paramTypes(method.getParameterTypes()) // 方法参数类型
                .build();
        // 创建RPC客户端
        RpcClient rpcClient = new RpcClient();
        // 发送RPC请求并返回结果
        return rpcClient.sendRequest(rpcRequest, host, port);
    }
}
