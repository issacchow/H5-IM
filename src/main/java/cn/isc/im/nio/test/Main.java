package cn.isc.im.nio.test;

public class Main {

    public static void main(String[] args) throws Exception {
        SocketServerTester helloWorldNIO = new SocketServerTester();
        helloWorldNIO.acceptByBlocking();
    }

}
