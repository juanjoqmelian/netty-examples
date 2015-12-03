package com.hotels.netty.balancer;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class LoadBalancer {

    static final RoundRobin ROUND_ROBIN = new RoundRobin();
    private final int port;

    public LoadBalancer(int port) {
        this.port = port;
    }


    public void run() throws InterruptedException {

        final EventLoopGroup bossGroup= new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            final ServerBootstrap loadBalancer = new ServerBootstrap()
                    .channel(NioServerSocketChannel.class)
                    .group(bossGroup, workerGroup)
                    .childOption(ChannelOption.AUTO_READ, false)
                    .childHandler(new LoadBalancerInitialiser());

            ChannelFuture future = loadBalancer.bind(port).sync();

            future.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws InterruptedException {

        new LoadBalancer(8080).run();
    }
}
