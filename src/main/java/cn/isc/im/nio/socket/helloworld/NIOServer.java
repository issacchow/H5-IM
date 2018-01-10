package cn.isc.im.nio.socket.helloworld;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class NIOServer {
    /**
     * 自己的ID
     */
    private String self;

    /**
     * 接收人ID
     */
    private String to;

    //通道管理器
    private Selector selector;

    private ByteBuffer writeBuffer = ByteBuffer.allocate(512);

    private SocketChannel channel;

    private Object lock = new Object();


    private volatile boolean isInit = false;


    public NIOServer(String self, String to)  {
//        super();
        this.self = self;
        this.to = to;
    }

    /**
     * 获得一个Socket通道，并对该通道做一些初始化的工作
     * @param ip 连接的服务器的ip
     * @param port  连接的服务器的端口号
     * @throws IOException
     */
    public void initClient(String ip,int port) throws IOException {
        // 获得一个Socket通道
        channel = SocketChannel.open();
        // 设置通道为非阻塞
        channel.configureBlocking(false);
        // 获得一个通道管理器
        this.selector = Selector.open();

        // 客户端连接服务器,其实方法执行并没有实现连接，需要在listen（）方法中调
        //用channel.finishConnect();才能完成连接
        channel.connect(new InetSocketAddress(ip,port));
        //将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_CONNECT事件。
        channel.register(selector, SelectionKey.OP_CONNECT);
    }

    /**
     * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public void listen() throws IOException {

        // 轮询访问selector
        while (true) {
            synchronized (lock) {
            }
            selector.select();
            // 获得selector中选中的项的迭代器
            Iterator<SelectionKey> ite = this.selector.selectedKeys().iterator();
            while (ite.hasNext()) {
                SelectionKey key =  ite.next();
                // 删除已选的key,以防重复处理
                //ite.remove();
                // 连接事件发生
                if (key.isConnectable()) {
                    SocketChannel channel = (SocketChannel) key
                            .channel();
                    // 如果正在连接，则完成连接
                    if(channel.isConnectionPending()){
                        channel.finishConnect();

                    }
                    // 设置成非阻塞
                    channel.configureBlocking(false);


                    //在和服务端连接成功之后，为了可以接收到服务端的信息，需要给通道设置读的权限。
                    channel.register(this.selector, SelectionKey.OP_READ);
                    isInit = true;
                    // 获得了可读的事件

                } else if (key.isReadable()) {
                    read(key);
                }

            }

        }
    }
    /**
     * 处理读取服务端发来的信息的事件
     * @param key
     * @throws IOException
     */
    public void read(SelectionKey key) throws IOException{

        SocketChannel data = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(512) ;
        try {
            data.read(buffer );

        } catch (IOException e) {
            e.printStackTrace();
            data.close();
            return;
        }
        buffer.flip();

        byte[] msgByte = new byte[buffer.limit()];
        buffer.get(msgByte);

//        Message msg = Message.getMsg(new String(msgByte));
//        System.out.println("---收到消息--"+msg+" 来自 "+msg.getFrom());

    }


    private void sendMsg(String content){
        writeBuffer.put(content.getBytes());
        writeBuffer.flip();
        try {
            while (writeBuffer.hasRemaining()) {
                channel.write(writeBuffer);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        writeBuffer.clear();
    }

    /**
     * 启动客户端测试
     * @throws IOException
     */
    public  void start() throws IOException {
        initClient("localhost",10087);
        new Thread("reading"){
            public void run() {
                try {
                    listen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
        }.start();

        int time3  = 0;

        while(!isInit&&time3<3){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time3 ++;
        }

        System.out.println("--------开始注册------");
//        Message re = new Message("", self, "");
//        sendMsg(re.toString());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-----注册成功----");

        String content ="";
        System.out.println("---- 请输入要发送的消息，按回车发送，输入 123 退出----------");

        Scanner s = new Scanner(System.in);

        while (!content.equals("123")&&s.hasNext()) {
            content = s.next();
//            Message msg = new Message(content, self, to);
//            msg.setType("1");
//            sendMsg(msg.toString());
            if (content.equals("123")) {
                break;
            }
            System.out.println("---发送成功---");

        }

        channel.close();
    }
}
