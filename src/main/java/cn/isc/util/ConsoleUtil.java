package cn.isc.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleUtil {

    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat();

    public static void log(String msg) {
        System.out.println();
        Date now = new Date();
        String s = dateTimeFormat.format(now);
        System.out.printf("[%s]", s);
        System.out.print(msg);
    }

    public static void log(String format, Object... args) {
        String msg = String.format(format, args);
        log(msg);
    }
}
