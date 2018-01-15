package cn.isc.im.netty.helloworld;

import cn.isc.im.nio.socket.helloworld.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import static cn.isc.util.ConsoleUtil.*;


public class NettyClient {


    static public void main(String[] args) throws InterruptedException {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap();
        MyChannelHandler handler = new MyChannelHandler();

        client.channel(NioSocketChannel.class)
                .group(eventLoopGroup)
                .handler(handler)
                .remoteAddress(Config.ServerAddress);

        log("bind port : 10087");
        ChannelFuture bindFuture = client.bind(10087);
        log("start connect...");
        ChannelFuture connectFuture = client.connect(Config.ServerAddress);
        log("connected");

        connectFuture.channel().close().sync();
    }


    public static class MyChannelHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log("exceptionCaught");
            super.exceptionCaught(ctx, cause);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            log("channelReadComplete");
            super.channelReadComplete(ctx);
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            log("channelRegistered");
            super.channelRegistered(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            log("channelUnregistered");
            super.channelUnregistered(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log("channelActive");
            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            log("channelInactive");
            super.channelInactive(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log("channelRead");
            super.channelRead(ctx, msg);
        }
    }


}
