package com.huang.gai.core.handler;


import com.huang.gai.rpc.model.RpcRequest;
import com.huang.gai.rpc.model.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author HuangGai
 */
public class PoisonRpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final Map<String, Object> handlerMap;

    public PoisonRpcHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }


    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {

        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            //根据request来处理具体的业务调用
            Object result = handle(request);
            response.setResult(result);
        } catch (Throwable t) {
            response.setError(t);
        }
        //写入 outbundle（即RpcEncoder）进行下一步处理（即编码）后发送到channel中给客户端
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handle(RpcRequest request) throws Throwable {
        String className = request.getClassName();

        //拿到实现类对象
        Object serviceBean = handlerMap.get(className);

        //拿到要调用的方法名、参数类型、参数值
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        //拿到接口类
        Class<?> forName = Class.forName(className);

        //调用实现类对象的指定方法并返回结果
        Method method = forName.getMethod(methodName, parameterTypes);
        return method.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // TODO: 2019/11/5 打印日志
        ctx.close();
    }
}
