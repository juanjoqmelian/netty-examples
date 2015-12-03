package com.hotels.netty.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class NettyProxyServer {

    private final int port;

    public NettyProxyServer(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {

        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            final ServerBootstrap server = new ServerBootstrap()
                    .channel(NioServerSocketChannel.class)
                    .group(bossGroup, workerGroup)
                    .childOption(ChannelOption.AUTO_READ, false)
                    .childHandler(new ProxyServerInitialiser());

            ChannelFuture future = server.bind(port).sync();

            future.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws InterruptedException {

        new NettyProxyServer(8080).run();
    }
}
