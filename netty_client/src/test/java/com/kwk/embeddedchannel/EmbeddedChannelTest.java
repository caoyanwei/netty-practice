package com.kwk.embeddedchannel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class EmbeddedChannelTest {
    @Test
    public void test1() {
        LineBasedFrameDecoder decoder = new LineBasedFrameDecoder(1024);
        EmbeddedChannel channel = new EmbeddedChannel(decoder);
        byte[] bytes = "abc\r\ndef\r\nhhhh\r\nd".getBytes();
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(bytes);
        ByteBuf input = buf.duplicate();

        //不能解码时返回false
        Assert.assertFalse(channel.writeInbound(input.readBytes(2)));
        Assert.assertTrue(channel.writeInbound(input.readBytes(input.readableBytes())));
        Assert.assertTrue(channel.finish());

        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertEquals(buf.skipBytes(2).readBytes(3), channel.readInbound());
        Assert.assertEquals(buf.skipBytes(2).readBytes(4), channel.readInbound());
        Assert.assertEquals(null, channel.readInbound());
    }

    @Test
    public void test2() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buf.writeInt(i * -1);
        }

        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        Assert.assertTrue(channel.writeOutbound(buf));
        Assert.assertTrue(channel.finish());

        ByteBuf output = channel.readOutbound();
        for (int i = 1; i < 10; i++) {
            Assert.assertEquals(i, output.readInt());
        }
        Assert.assertFalse(output.isReadable());
        Assert.assertNull(channel.readOutbound());
    }


    @Test(expected = TooLongFrameException.class)
    public void test3() {
        LineBasedFrameDecoder decoder = new LineBasedFrameDecoder(3);
        EmbeddedChannel channel = new EmbeddedChannel(decoder);
        byte[] bytes = "abc\r\ndef\r\nhhhh\r\nd".getBytes();
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(bytes);
        ByteBuf input = buf.duplicate();

        //不能解码时返回false
        channel.writeInbound(input);


        ReadTimeoutHandler a;
        ChannelHandlerAdapter b;
        SimpleChannelInboundHandler c;
    }
}

class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= 4) {
            int value = Math.abs(in.readInt());
            out.add(value);
        }
    }
}
