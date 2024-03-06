package wfb.rpc.core.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wfb.rpc.core.registry.ServiceRegistry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private final ExecutorService threadPool;
    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;

    /**
     * RpcServer 类的构造方法，初始化服务注册表和创建线程池以处理请求。
     *
     * @param serviceRegistry 用于注册和查找服务的服务注册表。
     */
    public RpcServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;

        // 创建一个阻塞队列来存储任务
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);

        // 使用默认的线程工厂创建线程池
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        // 创建线程池，设置核心线程数、最大线程数和线程空闲时间等参数
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    /**
     * 启动服务器，创建 ServerSocket 并接受传入连接。
     * 使用线程池处理传入的请求。
     *
     * @param port 服务器监听的端口号。
     */
    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器启动……");
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                logger.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());

                // 使用线程池执行新的请求处理线程
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry));
            }

            // 关闭线程池
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生:", e);
        }
    }

}