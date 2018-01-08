package cn.isc.im.nio.socket.helloworld;

import static cn.isc.util.ConsoleUtil.*;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.*;


public class ServerWithAcceptMode {
    Charset utf8 = Charset.forName("UTF-8");

    public ServerWithAcceptMode() {


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

        BlockingQueue queue = new ArrayBlockingQueue(30);
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
        Boolean enable = true;


        public OnConnection(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
            this.buffer = ByteBuffer.allocate(1024);
            readContent = new StringBuilder(1024);
        }


        @Override
        public void run() {

            CharsetDecoder decoder = utf8.newDecoder();
            CharsetEncoder encoder = utf8.newEncoder();


            String welcome = "Welcome to Connect Server Socket";
            CharBuffer charBuffer = CharBuffer.wrap(welcome.toCharArray());
            buffer.clear();
            //buffer.put(welcome.getBytes());
            try {
                this.socketChannel.write(encoder.encode(charBuffer));
            } catch (IOException e) {
                e.printStackTrace();
            }



            while (enable) {
                try {
                    log("Channel[%s]: wait for read", this.socketChannel.getRemoteAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    int readBytes = this.socketChannel.read(buffer);
                    if (readBytes > 0) {
                        log("Channel[%s]:has read bytes %s", this.socketChannel.getRemoteAddress(), readBytes);
                        buffer.flip();
                        String s = buffer.asCharBuffer().toString();
                        readContent.append(s);
                        buffer.clear();

                        continue;
                    }

                    if(readBytes==0){
                        log("Channel[%s]:read complete,bytes: %s", this.socketChannel.getRemoteAddress(), readBytes);
                        log("read content:%s", readContent);
                        //重置content值
                        this.readContent.delete(0, this.readContent.length());
                        buffer.clear();
                    }

                    if(readBytes==-1) {
                       log("Disconnect");
                       enable = false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    enable = false;

                }
            }

            log("Finish");
        }
    }


}
