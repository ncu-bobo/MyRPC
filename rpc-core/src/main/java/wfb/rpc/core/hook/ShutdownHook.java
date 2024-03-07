package wfb.rpc.core.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wfb.rpc.common.factory.ThreadPoolFactory;
import wfb.rpc.common.util.NacosUtil;

public class ShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    //单例模式，确保整个应用程序中只有一个ShutdownHook实例，从而避免可能导致的资源管理混乱
    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    // 添加钩子，用于注销服务和关闭线程池
    public void addClearAllHook() {
        logger.info("请注意，关闭后将自动注销所有服务。");
    // 在JVM被关闭时调用
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }

}
