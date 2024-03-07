package wfb.rpc.core.net.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import wfb.rpc.core.AbstractRpcServer;
import wfb.rpc.core.codec.CommonDecoder;
import wfb.rpc.core.codec.CommonEncoder;
import wfb.rpc.core.hook.ShutdownHook;
import wfb.rpc.core.provider.ServiceProviderImpl;
import wfb.rpc.core.registry.NacosServiceRegistry;
import wfb.rpc.core.serializer.CommonSerializer;

import java.util.concurrent.TimeUnit;


public class NettyServer extends AbstractRpcServer {

    private final CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public NettyServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        // 扫描并注册服务
        scanServices();
    }

    @Override
    public void start() {
        // 注册JVM关闭钩子，以在JVM关闭之前清理资源
        ShutdownHook.getShutdownHook().addClearAllHook();

        // 创建bossGroup用于处理连接请求，workerGroup用于处理与各个客户端连接的I/O操作
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建服务器启动对象，配置服务器参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup) // 设置主和工作线程组
                    .channel(NioServerSocketChannel.class) // 设置通道实现类型
                    .handler(new LoggingHandler(LogLevel.INFO)) // 设置日志处理器
                    .option(ChannelOption.SO_BACKLOG, 256) // 设置服务器接受连接的队列长度
                    .option(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动连接状态
                    .childOption(ChannelOption.TCP_NODELAY, true) // 设置I/O操作的处理方式
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 创建一个新的Channel管道初始化对象
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline(); // 获取管道
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS)) // 添加空闲状态处理器
                                    .addLast(new CommonEncoder(serializer)) // 添加自定义编码器
                                    .addLast(new CommonDecoder()) // 添加自定义解码器
                                    .addLast(new NettyServerHandler()); // 添加自定义处理器
                        }
                    });
            // 绑定服务器端口并同步等待成功，即启动服务器
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            // 对关闭通道进行监听
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            // 捕获到异常，记录错误日志
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            // 使用优雅停机方式关闭线程组，释放资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
