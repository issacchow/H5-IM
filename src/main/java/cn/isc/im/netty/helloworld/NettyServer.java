package cn.isc.im.netty.helloworld;

import cn.isc.im.channel.HelloWorldChannel;
import cn.isc.im.nio.socket.helloworld.Config;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import  static  cn.isc.util.ConsoleUtil.*;

public class NettyServer {

    static public void  main(String[] args){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        HelloWorldChannel handler = new HelloWorldChannel();
        bootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(handler);
        ChannelFuture future = bootstrap.register();

        log("start listen");
        bootstrap.bind(Config.ServerPort);
        log("listening..");
    }
}
