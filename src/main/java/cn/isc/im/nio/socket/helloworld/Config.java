package cn.isc.im.nio.socket.helloworld;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public final class Config {

    public static final int ServerPort = 10086;
    public static final SocketAddress ServerAddress = new InetSocketAddress("127.0.0.1",10086);
}
