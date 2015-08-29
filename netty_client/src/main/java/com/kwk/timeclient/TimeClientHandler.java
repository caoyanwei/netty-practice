package com.kwk.timeclient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;

public class TimeClientHandler extends ChannelHandlerAdapter {
    private int counter;


    public TimeClientHandler() {
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        byte[] req = ("QUERY TIME ORDER" + "\r\n").getBytes();
        ByteBuf buf;
        for (int i = 0; i < 1; i++) {
            buf = Unpooled.buffer(req.length);
            buf.writeBytes(req);
//            ctx.writeAndFlush(buf);//注意差异
//            ctx.channel().writeAndFlush(buf);
//            ctx.pipeline().writeAndFlush(buf);
        }

        ctx.fireChannelActive();

        ctx.executor().schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("close");
                ctx.close();
            }
        }, 3, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        ++counter;
        System.out.println("Now is " + body + ", counter: " + counter);
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
