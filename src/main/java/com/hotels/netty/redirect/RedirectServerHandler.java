package com.hotels.netty.redirect;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;


public class RedirectServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {

            final HttpRequest request = (HttpRequest) msg;

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.TEMPORARY_REDIRECT);

            if (request.getUri().contains("google")) {

                response.headers().add("Location", "http://www.google.co.uk");

            } else if (request.getUri().contains("amazon")) {

                response.headers().add("Location", "http://www.amazon.co.uk");

            }

            ChannelFuture future = ctx.write(response);

            future.addListener(ChannelFutureListener.CLOSE);

            System.out.println("Redirecting...");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
