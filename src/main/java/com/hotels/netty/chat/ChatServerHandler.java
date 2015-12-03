package com.hotels.netty.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;


public class ChatServerHandler extends ChannelInboundHandlerAdapter {

    private static final ChannelGroup channels = new DefaultChannelGroup("all-connected", null);


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[NETTY-CHAT-SERVER] - " + ctx.channel().remoteAddress() + " has joined the room!");
        channels.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[NETTY-CHAT-SERVER] - " + ctx.channel().remoteAddress() + " has left the room!");
        channels.remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.print("[" + ctx.channel().remoteAddress() + "] - " + msg);

        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            if (channel != incoming) {
                channel.writeAndFlush("[" + ctx.channel().remoteAddress() + "] - " + msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
