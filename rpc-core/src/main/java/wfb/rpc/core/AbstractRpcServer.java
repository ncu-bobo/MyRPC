package wfb.rpc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wfb.rpc.core.annotation.Service;
import wfb.rpc.core.annotation.ServiceScan;
import wfb.rpc.common.enumeration.RpcError;
import wfb.rpc.common.exception.RpcException;
import wfb.rpc.core.provider.ServiceProvider;
import wfb.rpc.core.registry.ServiceRegistry;
import wfb.rpc.common.util.ReflectUtil;

import java.net.InetSocketAddress;
import java.util.Set;

public abstract class AbstractRpcServer implements RpcServer {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    //Socket或者nacos Server都需要用到这个，且没区别，因此具体实现放在抽象类中
    public void scanServices() {
        // 从栈底找到启动类
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            // 判断启动类是否已添加ServiceScan注解
            if(!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        // 获取启动类注解所指定的扫描包的位置
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        // 若未指定包名，则将启动类所在的包作为扫描路径
        if("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        // 利用反射从启动类的包下面对所有类进行遍历，寻找服务
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz : classSet) {
            // 若包含Service注解
            if(clazz.isAnnotationPresent(Service.class)) {
                // 获取serviceName，可能为空
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    // 创建类的实例对象
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                // 若serviceName为空，则遍历该类所实现的接口，将每个接口与该类注册服务
                if("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface: interfaces){
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                // 若不为空，直接使用ServiceName注册服务
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }

    // 发布服务
    @Override
    public <T> void publishService(T service, String serviceName) {
        // 添加到本地服务注册表
        serviceProvider.addServiceProvider(service, serviceName);
        // 注册到Nacos中
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }

}
