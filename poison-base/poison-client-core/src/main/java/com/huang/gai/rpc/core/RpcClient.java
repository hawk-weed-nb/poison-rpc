package com.huang.gai.rpc.core;

import com.huang.gai.rpc.model.RpcRequest;
import com.huang.gai.rpc.model.RpcResponse;
import com.huang.gai.rpc.util.RpcDecoder;
import com.huang.gai.rpc.util.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 框架的RPC 客户端（用于发送 RPC 请求）
 *
 * @author haunggai
 */
@Component
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private int port;

    private RpcResponse response;

    public RpcClient() {
    }

    private final Object obj = new Object();

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 链接服务端，发送消息
     *
     * @param request request
     * @return RpcResponse
     * @throws Exception
     */
    public RpcResponse send(RpcRequest request) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) {
                            // 向pipeline中添加编码、解码、业务处理的handler
                            channel.pipeline()
                                    //OUT - 1
                                    .addLast(new RpcEncoder(RpcRequest.class))
                                    //IN - 1
                                    .addLast(new RpcDecoder(RpcResponse.class))
                                    //IN - 2
                                    .addLast(RpcClient.this);
                        }
                    }).option(ChannelOption.SO_KEEPALIVE, true);
            // 链接服务器
            ChannelFuture future = bootstrap.connect(host, port).sync();
            //将request对象写入outbundle处理后发出（即RpcEncoder编码器）
            future.channel().writeAndFlush(request).sync();

            // 用线程等待的方式决定是否关闭连接
            // 其意义是：先在此阻塞，等待获取到服务端的返回后，被唤醒，从而关闭网络连接
            synchronized (obj) {
                obj.wait();
            }
            if (response != null) {
                future.channel().closeFuture().sync();
            }
            return response;
        } finally {
            group.shutdownGracefully();
        }
    }

    /**
     * 读取服务端的返回结果
     */
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) {
        this.response = response;

        synchronized (obj) {
            obj.notifyAll();
        }
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("client caught exception", cause);
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.response = rpcResponse;

        synchronized (obj) {
            obj.notifyAll();
        }
    }
}
