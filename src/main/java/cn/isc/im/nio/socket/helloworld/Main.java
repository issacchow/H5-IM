package cn.isc.im.nio.socket.helloworld;


import cn.isc.util.ConsoleUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.*;

public class Main {


    public static void main(String... args) {
//        String s = FileUtil.readResourceFile("content.txt");
//        ConsoleUtil.log(s);
//
        for (int i = 0; i < 100; i++) {
            Main main = new Main();
            try {
                main.client();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void server() throws Exception {
        ServerWithAcceptMode helloWorldNIO = new ServerWithAcceptMode();
        helloWorldNIO.acceptByBlocking();
    }

    @Test
    public void server2() throws Exception {
        ServerWithSelectorMode server = new ServerWithSelectorMode();
        server.runByNonBlocking();
    }

    @Test
    public void client() throws Exception {
        ClientWithSelector clientTester = new ClientWithSelector();
        clientTester.runSocketClient();
    }


    @Test
    public void client3() throws IOException {
        NIOServer server = new NIOServer("a", "b");
        server.start();
    }


    @Test
    public void futureTest() throws InterruptedException {
        ExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {

                ConsoleUtil.log("doing something...");
                Thread.sleep(2000);
                return 10086;
            }
        };

        Future<Integer> future = executorService.<Integer>submit(callable);
        ConsoleUtil.log("wait for doing something,is done:%s", future.isDone());
        Integer a = null;
        try {

            ConsoleUtil.log("wait for get ");
//            Thread.sleep(1000);//通过延时来达到中断正在执行的过程
//            future.cancel(true);
            a = future.get();//这里会阻塞线程
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ConsoleUtil.log("get %s", a);


        ConsoleUtil.log("is cancel:%s", future.isCancelled());
        ConsoleUtil.log("is done:%s", future.isDone());

    }


}
