package com.hotels.netty.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;


public class ProxyFrontendHandler extends ChannelInboundHandlerAdapter {

    private volatile Channel outboundChannel;
    private static final String GOOGLE_IP = "64.233.184.113";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        final Channel inboundChannel = ctx.channel();

        final Bootstrap connection = new Bootstrap()
                .channel(ctx.channel().getClass())
                .group(inboundChannel.eventLoop())
                .option(ChannelOption.AUTO_READ, false) //To avoid reading before the connection has been established
                .handler(new ProxyBackendHandler(inboundChannel));

        ChannelFuture future = connection.connect(GOOGLE_IP, 80);

        outboundChannel = future.channel();

        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("Connected to google.co.uk");
                    inboundChannel.read();
                } else {
                    System.out.println("Something went wrong connecting to google.co.uk");
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
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        ctx.channel().read();
                    } else {
                        future.channel().close();
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
