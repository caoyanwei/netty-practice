package com.kwk.timeserver;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

public class TimeServerHandler extends ChannelHandlerAdapter {
    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String body = (String) msg;
        ++counter;
        System.out.println("rsv: " + body + ", counter: " + counter);
        String rsp = "QUERY TIME ORDER".equals(body) ? new Date().toString() : "Bad Order";
        rsp = rsp + "\r\n";
        ByteBuf buf = Unpooled.copiedBuffer(rsp.getBytes());
        ctx.writeAndFlush(buf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
