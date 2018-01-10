package cn.isc.im.nio.socket.helloworld;


import org.junit.Test;

import java.io.IOException;

public class Main {


    public static void main(String ...args){
        Main main = new Main();
        try {
            main.client();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void server() throws Exception {
        ServerWithAcceptMode helloWorldNIO = new ServerWithAcceptMode();
        helloWorldNIO.acceptByBlocking();
    }

    @Test
    public void server2() throws Exception {
        ServerWithSelectorMode server = new ServerWithSelectorMode();
        server.runByNonBlocking();
    }

    @Test
    public  void client() throws Exception {
        ClientWithSelector clientTester = new ClientWithSelector();
        clientTester.runSocketClient();
    }




    @Test
    public void client3() throws IOException {
        NIOServer server = new NIOServer("a","b");
        server.start();
    }


}
