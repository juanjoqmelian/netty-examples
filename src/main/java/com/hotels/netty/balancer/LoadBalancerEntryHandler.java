package com.hotels.netty.balancer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class LoadBalancerEntryHandler extends ChannelInboundHandlerAdapter {

    private volatile Channel outboundChannel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        final Channel inboundChannel = ctx.channel();

        final Bootstrap connection = new Bootstrap()
                .channel(ctx.channel().getClass())
                .group(inboundChannel.eventLoop())
                .handler(new LoadBalancerConnectionHandler(inboundChannel));

        final Instance instance = LoadBalancer.ROUND_ROBIN.getNext();

        ChannelFuture future = connection.connect(instance.getHost(), instance.getPort());

        outboundChannel = future.channel();

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("Connected to instance " + instance);
                    inboundChannel.read();
                } else {
                    System.out.println("Something went wrong trying to connect to instance " + instance);
                    inboundChannel.close();
                }
            }
        });
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {

        if (outboundChannel.isActive()) {

            ChannelFuture future = outboundChannel.writeAndFlush(msg);

            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        ctx.channel().read();
                    } else {
                        ctx.channel().close();
                    }
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
