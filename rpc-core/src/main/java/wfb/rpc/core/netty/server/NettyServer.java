package wfb.rpc.core.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wfb.rpc.core.RpcServer;
import wfb.rpc.core.codec.CommonDecoder;
import wfb.rpc.core.codec.CommonEncoder;
import wfb.rpc.core.serializer.HessianSerializer;

public class NettyServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    @Override
    public void start(int port) {
        // 主事件循环组，处理连接事件
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 工作事件循环组，处理已连接的通道的I/O事件
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 服务器启动辅助类，用于配置服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup) // 设置主和工作事件循环组
                    .channel(NioServerSocketChannel.class) // 设置通道的实现类
                    .handler(new LoggingHandler(LogLevel.INFO)) // 设置日志处理器
                    .option(ChannelOption.SO_BACKLOG, 256) // 设置服务器接受连接的队列长度
                    .option(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动连接的状态
                    .childOption(ChannelOption.TCP_NODELAY, true) // 设置无延迟发送数据
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 设置通道初始化器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 添加编码器、解码器和业务处理器到pipeline
                            pipeline.addLast(new CommonEncoder(new HessianSerializer()));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            // 绑定端口并同步等待成功，即启动服务器
            ChannelFuture future = serverBootstrap.bind(port).sync();
            // 等待服务器通道关闭，即服务器关闭
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            // 记录错误日志：启动服务器时发生异常
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            // 优雅关闭事件循环组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}

