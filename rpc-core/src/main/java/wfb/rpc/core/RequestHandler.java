package wfb.rpc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wfb.rpc.common.entity.RpcRequest;
import wfb.rpc.common.entity.RpcResponse;
import wfb.rpc.common.enumeration.ResponseCode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {

    // 定义日志记录器
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    // 处理RPC请求
    public Object handle(RpcRequest rpcRequest, Object service) {
        Object result = null;
        try {
            // 调用目标方法
            result = invokeTargetMethod(rpcRequest, service);
            // 记录服务调用成功的日志
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (IllegalAccessException | InvocationTargetException e) {
            // 记录服务调用失败的日志
            logger.error("调用或发送时有错误发生：", e);
        }
        return result;
    }

    // 调用目标方法
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws IllegalAccessException, InvocationTargetException {
        Method method;
        try {
            // 根据方法名和参数类型获取方法
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            // 如果没有找到方法，返回方法未找到的响应
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }
        // 调用方法并返回结果
        return method.invoke(service, rpcRequest.getParameters());
    }

}
