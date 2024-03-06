package wfb.rpc.core.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wfb.rpc.common.exception.RpcException;
import wfb.rpc.common.enumeration.RpcError;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// ServiceRegistry的默认实现类
public class DefaultServiceRegistry implements ServiceRegistry {

    // 定义日志记录器
    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);

    // 用于存储服务实例的映射，保证线程安全
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    // 记录已注册服务的名称，保证线程安全
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    // 注册服务方法，线程安全
    @Override
    public synchronized <T> void register(T service) {
        // 获取服务类的全限定名
        String serviceName = service.getClass().getCanonicalName();
        // 如果服务已注册，则不重复注册
        if(registeredService.contains(serviceName)) return;
        // 添加服务到已注册服务集合
        registeredService.add(serviceName);
        // 获取服务实现的接口
        Class<?>[] interfaces = service.getClass().getInterfaces();
        // 如果没有实现任何接口，则抛出异常
        if(interfaces.length == 0) {
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        // 将服务按接口全限定名注册到服务映射中
        for(Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service);
        }
        // 记录注册信息到日志
        logger.info("向接口: {} 注册服务: {}", Arrays.toString(interfaces), serviceName);
    }

    // 根据服务名称获取服务实例，线程安全
    @Override
    public synchronized Object getService(String serviceName) {
        // 从服务映射中获取服务实例
        Object service = serviceMap.get(serviceName);
        // 如果服务未找到，则抛出异常
        if(service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        // 返回服务实例
        return service;
    }
}


