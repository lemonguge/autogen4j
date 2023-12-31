package cn.homj.autogen4j.support;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jiehong.jh
 * @date 2023/12/15
 */
public class LogUtils {
    /**
     * 禁用日志输出
     */
    public static boolean disableLogger = false;
    /**
     * 时间格式化
     */
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static boolean hasLogger = false;

    private static final Map<String, Logger> LOGGER_MAP = new ConcurrentHashMap<>();

    static {
        try {
            Class.forName("org.slf4j.Logger");
            hasLogger = true;
        } catch (ClassNotFoundException e) {
            // do nothing
        }
    }

    private static Logger getLogger(String name) {
        Logger logger = LOGGER_MAP.get(name);
        if (logger == null) {
            logger = LOGGER_MAP.computeIfAbsent(name, LoggerFactory::getLogger);
        }
        return logger;
    }

    private static String format(String format, Object... args) {
        StringBuilder buf = new StringBuilder(format);
        int i = 0;
        while (i < args.length) {
            int j = buf.indexOf("{}");
            if (j < 0) {
                break;
            }
            buf.replace(j, j + 2, args[i++].toString());
        }
        return buf.toString();
    }

    private static String now() {
        return LocalDateTime.now().format(formatter);
    }

    public static void info(String name, String message) {
        if (!disableLogger && hasLogger) {
            getLogger(name).info(message);
        } else {
            System.out.println("[" + now() + "] INFO : " + message);
        }
    }

    public static void info(String name, String format, Object arg) {
        if (!disableLogger && hasLogger) {
            getLogger(name).info(format, arg);
        } else {
            System.out.println("[" + now() + "] INFO : " + format(format, arg));
        }
    }

    public static void info(String name, String format, Object arg, Object arg2) {
        if (!disableLogger && hasLogger) {
            getLogger(name).info(format, arg, arg2);
        } else {
            System.out.println("[" + now() + "] INFO : " + format(format, arg, arg2));
        }
    }

    public static void info(String name, String format, Object... args) {
        if (!disableLogger && hasLogger) {
            getLogger(name).info(format, args);
        } else {
            System.out.println("[" + now() + "] INFO : " + format(format, args));
        }
    }

    public static void warn(String name, String message) {
        if (!disableLogger && hasLogger) {
            getLogger(name).warn(message);
        } else {
            System.out.println("[" + now() + "] WARN : " + message);
        }
    }

    public static void error(String name, String message, Throwable t) {
        if (!disableLogger && hasLogger) {
            getLogger(name).error(message, t);
        } else {
            System.err.println("[" + now() + "] ERROR: " + message);
            t.printStackTrace();
        }
    }
}
