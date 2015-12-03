package com.hotels.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;


public class TimeEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        UnixTime unixTime = (UnixTime) msg;

        ByteBuf encoded = ctx.alloc().buffer(4);
        encoded.writeInt((int) unixTime.value());
        ctx.write(encoded, promise);
    }
}

