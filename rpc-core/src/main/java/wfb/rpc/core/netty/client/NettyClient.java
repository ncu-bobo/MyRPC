package wfb.rpc.core.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wfb.rpc.common.entity.RpcRequest;
import wfb.rpc.common.entity.RpcResponse;
import wfb.rpc.core.RpcClient;
import wfb.rpc.core.codec.CommonDecoder;
import wfb.rpc.core.codec.CommonEncoder;
import wfb.rpc.core.serializer.KryoSerializer;

public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private String host;
    private int port;
    private static final Bootstrap bootstrap;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    static {
        // 创建一个NioEventLoopGroup实例用于处理所有的I/O操作
        EventLoopGroup group = new NioEventLoopGroup();
        // 创建Bootstrap实例，Bootstrap是Netty用于启动客户端的辅助类
        bootstrap = new Bootstrap();
        bootstrap.group(group) // 设置EventLoopGroup，它提供了用于处理Channel事件的EventLoop
                .channel(NioSocketChannel.class) // 指定Channel的实现类
                .option(ChannelOption.SO_KEEPALIVE, true) // 设置Channel选项，保持连接活跃
                .handler(new ChannelInitializer<SocketChannel>() { // 设置ChannelHandler
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 添加自定义的解码器、编码器和处理器到pipeline，选用的是kryo序列化
                        pipeline.addLast(new CommonDecoder())
                                .addLast(new CommonEncoder(new KryoSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                });
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try {
            // 异步连接到服务器并等待连接完成
            ChannelFuture future = bootstrap.connect(host, port).sync();
            // 记录日志：客户端成功连接到服务器
            logger.info("客户端连接到服务器 {}:{}", host, port);
            Channel channel = future.channel();
            if(channel != null) {
                // 异步发送RPC请求到服务器并添加监听器处理发送结果
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if(future1.isSuccess()) {
                        // 记录日志：客户端成功发送消息
                        logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        // 记录错误日志：发送消息时出错
                        logger.error("发送消息时有错误发生: ", future1.cause());
                    }
                });
                // 等待Channel被关闭
                channel.closeFuture().sync();
                // 从Channel中获取RPC响应并返回结果
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }

        } catch (InterruptedException e) {
            // 记录错误日志：发送消息时中断异常
            logger.error("发送消息时有错误发生: ", e);
        }
        return null;
    }


}

