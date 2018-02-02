package cn.isc.im.netty.helloworld;

import cn.isc.im.nio.socket.helloworld.Config;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import static cn.isc.util.ConsoleUtil.log;

public class NettyServer {

    static public void  main(String[] args){

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        final ConnectionHandler handler = new ConnectionHandler();
        try {
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("OnConnected_Handler", handler);
                        }
                    });

            //ChannelFuture future = bootstrap.register();

            log("start listen");
            ChannelFuture future = bootstrap.bind(Config.ServerPort).sync();
            log("listening..");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }

    private static class ConnectionHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf in = (ByteBuf)msg;
            String content = in.toString(CharsetUtil.UTF_8);
            log("receive message:\n%s",content);
            //super.channelRead(ctx, msg);
            //ctx.
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            //super.channelReadComplete(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            //super.exceptionCaught(ctx, cause);
            cause.printStackTrace();
            ctx.close();
        }
    }
}
