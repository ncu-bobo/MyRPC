package wfb.rpc.common.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wfb.rpc.common.enumeration.RpcError;
import wfb.rpc.common.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 管理Nacos连接等工具类
 */
public class NacosUtil {

    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);

    private static final NamingService namingService;
    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address;

    private static final String SERVER_ADDR = "127.0.0.1:8848";

    static {
        namingService = getNacosNamingService();
    }

    // 获取Nacos命名服务的静态方法
    public static NamingService getNacosNamingService() {
        try {
            // 尝试创建并返回一个Nacos命名服务实例，SERVER_ADDR是Nacos服务器地址
            return NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            // 如果连接Nacos时发生异常，则记录错误并抛出自定义的RpcException异常
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    // 注册服务到Nacos的静态方法
    public static void registerService(String serviceName, InetSocketAddress address) throws NacosException {
        // 使用Nacos命名服务注册服务实例，包括服务名、主机名和端口
        namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
        // 将服务地址保存到静态变量，用于之后可能的操作
        NacosUtil.address = address;
        // 将服务名添加到已注册服务名的集合中
        serviceNames.add(serviceName);
    }

    // 获取指定服务的所有实例的静态方法
    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        // 通过Nacos命名服务获取指定服务名的所有服务实例
        return namingService.getAllInstances(serviceName);
    }

    // 从Nacos清除注册的服务的静态方法
    public static void clearRegistry() {
        // 如果已注册服务名集合不为空，并且服务地址不为null
        if(!serviceNames.isEmpty() && address != null) {
            // 获取服务地址的主机名和端口
            String host = address.getHostName();
            int port = address.getPort();
            // 迭代已注册的服务名
            Iterator<String> iterator = serviceNames.iterator();
            while(iterator.hasNext()) {
                String serviceName = iterator.next();
                try {
                    // 尝试注销每个服务
                    namingService.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    // 如果注销失败，则记录错误
                    logger.error("注销服务 {} 失败", serviceName, e);
                }
            }
        }
    }

}
