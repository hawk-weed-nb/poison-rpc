package com.huang.gai.rpc.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RPC 编码器
 *
 * @author haunggai
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;

    /**
     * 构造函数传入向反序列化的class
     *
     * @param genericClass genericClass
     */
    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object inob, ByteBuf out) {
        //序列化
        if (genericClass.isInstance(inob)) {
            byte[] data = RpcSerializationUtils.serialize(inob);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}