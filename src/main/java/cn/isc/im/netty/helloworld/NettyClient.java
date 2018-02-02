package cn.isc.im.netty.helloworld;

import cn.isc.im.nio.socket.helloworld.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static cn.isc.util.ConsoleUtil.*;


public class NettyClient {


    static public void main(String[] args) throws InterruptedException, IOException {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap();
        MyChannelHandler handler = new MyChannelHandler();

        client.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(handler)
                .remoteAddress(Config.ServerAddress);

        log("bind port : 10087");
        ChannelFuture bindFuture = client.bind(10087);
        log("start connect...");
        ChannelFuture connectFuture = client.connect(Config.ServerAddress).sync();
        log("connected");


        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Charset charset = Charset.forName("UTF-8");
        int offset = 0;
        int length = 0;
        boolean exit = false;
        while (exit == false) {
            String s = reader.readLine();
            if ("quit".equals(s)) {
                connectFuture.channel().close().sync();
                exit = true;
                continue;
            }

            //发送字节
            byte[] bytes = s.getBytes(charset);

            ByteBuffer xx = ByteBuffer.wrap(bytes);


            Channel channel = connectFuture.channel();
            channel.writeAndFlush(xx);
        }
    }


    @ChannelHandler.Sharable
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
