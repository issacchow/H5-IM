package cn.isc.im.nio.test;

import cn.isc.util.ConsoleUtil;

import static cn.isc.util.ConsoleUtil.*;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class SocketServerTester {
    Charset utf8 = Charset.forName("utf-8");

    public SocketServerTester() {


    }

    public void acceptByBlocking() throws IOException {
        runAsAccept(true);
    }

    public void acceptByNonBlocking() throws IOException {
        runAsAccept(false);
    }

    private void runAsAccept(boolean isBlocking) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(Config.ServerPort)).configureBlocking(isBlocking);

        ArrayBlockingQueue queue = new ArrayBlockingQueue(30);
        ExecutorService executor = new ThreadPoolExecutor(3, 3, 1, TimeUnit.DAYS, queue);


        while (true) {
            log("Start accept");
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel == null) {
                log("new Connection is null");
            } else {
                log("new Connection,ip:%s", socketChannel.getRemoteAddress());
                OnConnection connection = new OnConnection(socketChannel);
                executor.execute(connection);
            }

        }

    }

    private class OnConnection implements Runnable {

        SocketChannel socketChannel = null;
        ByteBuffer buffer = null;
        StringBuilder readContent = null;

        public OnConnection(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
            this.buffer = ByteBuffer.allocate(4);
            readContent = new StringBuilder(1024);
        }


        @Override
        public void run() {

            CharsetDecoder decoder = utf8.newDecoder();

            while (true) {
                try {
                    log("Channel[%s]: wait for read", this.socketChannel.getRemoteAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    int readBytes = this.socketChannel.read(buffer);
                    if (readBytes > 0) {
                        log("Channel[%s]:has read bytes %s", this.socketChannel.getRemoteAddress(), readBytes);

                        readContent.append(utf8.decode(buffer).toString());

                    } else {
                        log("Channel[%s]:read complete,bytes: %s", this.socketChannel.getRemoteAddress(), readBytes);
                        log("read content:%s", readContent);
                        //重置content值
                        this.readContent.delete(0, this.readContent.length());
                        buffer.clear();
                        buffer.flip();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
