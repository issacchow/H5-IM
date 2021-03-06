package cn.isc.im.nio.socket.helloworld;

import cn.isc.util.FileUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.isc.util.ConsoleUtil.log;

public class ClientWithSelector {
    private ByteBuffer buffer = ByteBuffer.allocate(4);
    private Object syncLocker = this;
    private Charset utf8 = Charset.forName("UTF-8");
    private CharsetDecoder decoder = utf8.newDecoder();
    private CharsetEncoder encoder = utf8.newEncoder();

    public void runSocketClient() throws IOException {
        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        //socketChannel.bind(new InetSocketAddress(9999))
        socketChannel.configureBlocking(false);
        Integer age = 30;
        socketChannel.register(selector, SelectionKey.OP_CONNECT, age);


        Boolean isConnect = socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(), Config.ServerPort));
//        Boolean isConnect = socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(), 10087));
        if (isConnect == false) {
            //return;
        }


        while (true) {
            log("on select");
            int i = selector.select();
            if (i <= 0) continue;
            log("select arriaay:%s", i);
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                if (key.isConnectable()) {
                    log("Connected");
                    // 如果正在连接，则完成连接
                    //只有调用了finishConnect后,selector.select 才会开始阻塞等待其他线程唤醒
                    if (socketChannel.isConnectionPending()) {
                        socketChannel.finishConnect();
                    }
                    // 设置成非阻塞
                    //socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    //readyToSend(socketChannel);
                    loopToSend(socketChannel);
                    continue;
                }
                if (key.isReadable()) {
                    log("on read");
                    onRead(key);
                    continue;
                }

                if (key.isWritable()) {
                    log("write");
                    continue;
                }
            }

        }//while

    }

    public void onRead(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        synchronized (syncLocker) {
            try {

                int readBytes = -1;
                StringBuilder content = new StringBuilder();
                do {
                    buffer.clear();
                    readBytes = socketChannel.read(buffer);
                    if (readBytes > 0) {
                        //将buffer position重置
                        buffer.flip();
                        String s = decoder.decode(buffer).toString();

                        content.append(s);
                    }
                } while (readBytes > 0);

                log("read: %s", content.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readyToSend(SocketChannel socketChannel) {

        BlockingQueue queue = new ArrayBlockingQueue(3);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.DAYS, queue);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                log("\nready to send:");
                while (true) {
                    try {
                        String s = reader.readLine();
                        byte[] bytes = s.getBytes(utf8);
                        buffer.clear();
                        for (int offset = 0; offset < bytes.length; offset++) {
                            buffer.put(bytes[offset]);
                            if (buffer.remaining() == 0) {
                                buffer.flip();
                                socketChannel.write(buffer);
                                buffer.clear();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    //定时发送数据
    private void loopToSend(SocketChannel socketChannel) {

        BlockingQueue queue = new ArrayBlockingQueue(3);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.DAYS, queue);
        executor.execute(new Runnable() {
            @Override
            public void run() {

                String sendContent = FileUtil.readResourceFile("content.txt",Charset.forName("UTF-8"));

                CharBuffer c = CharBuffer.wrap(sendContent);
                ByteBuffer buffer = null;
                try {
                    buffer = encoder.encode(c);
                    buffer.mark();
                } catch (CharacterCodingException e) {
                    e.printStackTrace();
                    return;
                }

                log("\nready to send:");
                while (true) {
                    try {
                        log("send");
                        buffer.reset();
                        socketChannel.write(buffer);
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

}
