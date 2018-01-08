package cn.isc.im.nio.socket.helloworld;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.isc.util.ConsoleUtil.log;

/**
 * 选择器模式socket服务器
 */
public class ServerWithSelectorMode {
    private Charset utf8 = Charset.forName("UTF-8");

    public ServerWithSelectorMode() {


    }

    public void runByBlocking() throws IOException {
        runAsSelectorMode(true);
    }

    public void runByNonBlocking() throws IOException {
        runAsSelectorMode(false);
    }

    private void runAsSelectorMode(boolean isBlocking) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(isBlocking);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        serverSocketChannel.bind(new InetSocketAddress(Config.ServerPort));

        ArrayBlockingQueue queue = new ArrayBlockingQueue(2);
        ExecutorService executor = new ThreadPoolExecutor(2, 2, 1, TimeUnit.DAYS, queue);

        Listen listen = new Listen(selector);
        //executor.execute(listen);
        listen.run();

    }

    private class Listen implements Runnable {

        Selector selector = null;
        ByteBuffer buffer = null;
        StringBuilder readContent = null;
        Boolean enable = true;
        Charset utf8 = Charset.forName("UTF-8");
        CharsetDecoder decoder = utf8.newDecoder();


        public Listen(Selector Selector) {
            this.selector = Selector;
            this.buffer = ByteBuffer.allocate(1024);
            readContent = new StringBuilder(1024);
        }


        @Override
        public void run() {



            while (enable) {
                try {
                    int keys = selector.select();
                    if (keys <= 0) {
                        log("keys is letter than Zero");
                        continue;
                    }

                    log("on selected...");
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();

                        if (key.isAcceptable()) {
                            log("accept a connection");
                            ServerSocketChannel sktChannel = (ServerSocketChannel) key.channel();
                            SocketChannel clientSkt = sktChannel.accept();
                            //为个链接创建固定的缓存
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            //使用同一条线程的selector 进行监听接收数据事件
                            clientSkt.configureBlocking(false);
                            clientSkt.register(selector, SelectionKey.OP_READ, buffer);
                            continue;
                        }

                        if (key.isReadable()) {
                            log("on read");
                            if(key.channel()==null){
                                key.cancel();
                                continue;
                            }
                            SocketChannel sktChannel = (SocketChannel) key.channel();
                            ByteBuffer buffer = (ByteBuffer) key.attachment();
                            int readBytes = -1;
                            StringBuilder msg = new StringBuilder();
                            do {
                                buffer.clear();
                                readBytes = sktChannel.read(buffer);
                                buffer.flip();
                                String s = decoder.decode(buffer).toString();
                                msg.append(s);

                            } while (readBytes > 0);
                            if(readBytes==-1){
                                log("disconnect");
                                key.cancel();
                                sktChannel.close();
                            }else{
                                log("%s read msg:%s", sktChannel.getRemoteAddress(), msg.toString());
                            }

                            continue;
                        }


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }


}
