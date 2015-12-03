package com.hotels.netty.balancer;


public class Instance {

    private final String host;
    private final int port;

    public Instance(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
