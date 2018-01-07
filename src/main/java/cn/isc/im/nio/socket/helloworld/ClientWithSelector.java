package cn.isc.im.nio.socket.helloworld;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

import static cn.isc.util.ConsoleUtil.log;

public class ClientWithSelector {
    private ByteBuffer buffer = ByteBuffer.allocate(4);
    private Object syncLocker = this;
    private Charset utf8 = Charset.forName("UTF-8");
    private CharsetDecoder decoder = utf8.newDecoder();

    public void runSocketClient() throws IOException {
        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        //socketChannel.bind(new InetSocketAddress(9999))
        socketChannel.configureBlocking(false);
        Integer age = 30;
        socketChannel.register(selector, SelectionKey.OP_CONNECT, age);


//        Boolean isConnect = socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(),Config.ServerPort));
        Boolean isConnect = socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(), 10087));
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
                    it.remove();
                    continue;
                }
                if (key.isReadable()) {
                    log("on read");
                    read(key);
                    it.remove();
                    continue;
                }
            }

        }//while

    }

    public void read(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        synchronized (syncLocker) {
            try {

                int readBytes = -1;
                StringBuilder content = new StringBuilder();
               do {
                   buffer.clear();
                   readBytes = socketChannel.read(buffer);
                   if(readBytes>0){
                       //将buffer position重置
                       buffer.flip();
                       String s = decoder.decode(buffer).toString();

                       content.append(s);
                   }
               }while (readBytes>0);

               log("read: %s",content.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
