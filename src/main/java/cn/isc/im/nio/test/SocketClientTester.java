package cn.isc.im.nio.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

import static cn.isc.util.ConsoleUtil.log;

public class SocketClientTester {

    public void runSocketClient() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.bind(new InetSocketAddress(9999)).configureBlocking(false);
        Boolean isConnect = socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(),Config.ServerPort));
        if(isConnect==false){
            return;
        }


        Selector selector = Selector.open();
        Integer age = 30;
        SelectionKey key = socketChannel.register(selector, SelectionKey.OP_CONNECT & SelectionKey.OP_ACCEPT & SelectionKey.OP_READ & SelectionKey.OP_WRITE, age);
        while (true) {
            log("on select");
            int i = selector.select();
            if (i <= 0) continue;
            log("select arriaay:%s", i);

            Set<SelectionKey> keys = selector.selectedKeys();
            for (SelectionKey selectedKey : keys) {
                Object obj = selectedKey.attachment();
                log("attachedment:%s", obj);
            }

        }

    }

}
