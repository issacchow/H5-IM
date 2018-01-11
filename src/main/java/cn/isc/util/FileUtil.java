package cn.isc.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * Created by IssacChow on 18/1/11.
 */
public class FileUtil {

    public static Charset defaultCharset = Charset.defaultCharset();

    static public String readFile(String filePath){
        return readFile(filePath, defaultCharset);
    }

    /**
     * 从文件中读取String
     * @param filePath
     * @param charset
     * @return
     */
    static public String readFile(String filePath,Charset charset){
        try {
            RandomAccessFile file = new RandomAccessFile(filePath,"r");
            FileChannel fileChannel = file.getChannel();

            StringBuilder builder = new StringBuilder(1024);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            String s = null;
            int readBytes = -1;
            do {
                buffer.clear();
                readBytes = fileChannel.read(buffer);
                if(readBytes>-1){
                    buffer.flip();
                     s = charset.decode(buffer).toString();
                    builder.append(s);
                }

            }while (readBytes>-1);

            fileChannel.close();
            file.close();

            return builder.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 从资源文件中读取String
     * @param uri
     * @param charset
     * @return
     */
    static public String readResourceFile(String uri,Charset charset){
        URL url = Thread.currentThread().getContextClassLoader().getResource(uri);
        return readFile(url.getFile(),charset);
    }

    static public String readResourceFile(String uri){
        return  readResourceFile(uri,defaultCharset);
    }
}
