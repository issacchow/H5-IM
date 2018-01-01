package cn.isc.util;

import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleUtil {

    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat();
    public static void log(String format, Object... args) {
        System.out.println();
        Date now = new Date();
        String s = dateTimeFormat.format(now);
        System.out.printf("[%s]",s);
        System.out.printf(format, args);
    }
}
