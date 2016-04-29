package heelenyc.commonlib;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

public class LogUtils {
    
    private static Logger logger = Logger.getLogger(LogUtils.class);

    public static boolean enableConsole = true;

    public static boolean enableDebug = true;

    public static boolean enableInfo = true;

    public static boolean enableWarn = true;

    public static boolean enableError = true;

    public static boolean bufferedOutput = false;

    public static final ConcurrentMap<String, Counter> errorCounters = new ConcurrentHashMap<String, Counter>();

    public static final Counter errorCounter = new Counter();
    
    public static Logger getLogger(Class<?> c){
        return Logger.getLogger(c);
    }

    public static Logger createLogger(String name, String outputPath) {
        return createLogger(name, outputPath, new PatternLayout(), false, false);
    }

    public static Logger createLogger(String name, String outputPath, boolean additivity, boolean isDailyMode) {
        return createLogger(name, outputPath, new PatternLayout(), additivity, isDailyMode);
    }

    public static Logger createLogger(String name, String outputPath, PatternLayout layout, boolean additivity) {
        return createLogger(name, outputPath, layout, additivity, false);
    }

    public static Logger createDatetimeLogger(String name, String outputPath) {
        return createLogger(name, outputPath, new PatternLayout("%d %p [%c] \r\n\t%m%n"), false, false);
    }

    public static Logger createDatetimeLogger(String name, String outputPath, boolean additivity, boolean isDailyMode) {
        return createLogger(name, outputPath, new PatternLayout("%d %p [%c] \r\n\t%m%n"), additivity, isDailyMode);
    }

    public static Logger createLogger(String name, String outputPath, PatternLayout layout, boolean additivity, boolean isDailyMode) {
        Logger newLogger;
        try {
            if (!org.apache.commons.lang.StringUtils.isEmpty(outputPath)) {
                outputPath = outputPath.endsWith("/") ? outputPath : outputPath + "/";
            }

            String logPath = outputPath + name + ".log";
            // Create DailyRollingFileAppender if is daily mode.
            // Otherwise, create RollingFileAppender
            FileAppender appender = isDailyMode ? new DailyRollingFileAppender(layout, logPath, "'.'yyyy-MM-dd") : new RollingFileAppender(layout, logPath, true);

            /**
             * 加入BufferIO 设置Buffer大小为8K
             */
            if (bufferedOutput) {
                appender.setBufferedIO(true);
                appender.setBufferSize(8192);
            }

            newLogger = Logger.getLogger(name);
            newLogger.removeAllAppenders();
            // newLogger.addAppender(new ConsoleAppender(layout));
            newLogger.addAppender(appender);
            // is output into info.log file
            newLogger.setAdditivity(additivity);
        } catch (Exception e) {
            LogUtils.error(logger, e, "create Logger error! name: {0}", name);
            throw new RuntimeException();
        }

        return newLogger;
    }

    public static void appendConsole(Logger logger) {
        logger.addAppender(new ConsoleAppender());
    }

    @SuppressWarnings("deprecation")
    public static void console(String message, Object... params) {
        if (!enableConsole)
            return;

        String msg = StringUtils.format(message, params);
        System.out.println(new Date().toLocaleString() + " - " + msg);
    }

    public static void debug(Logger logger, String message, Object... params) {
        if (!enableDebug)
            return;

        logger.debug(StringUtils.format(message, params));
    }

    public static void debug(Logger logger, long startTime, long threshold, String message, Object... params) {
        if (!enableDebug)
            return;

        long costtime = isOnThreshold(startTime, threshold, message);
        if (costtime != -1) {
            message = formatCosttime(message, costtime);
            debug(logger, message, params);
        }
    }

    public static void info(Logger logger, String message, Object... params) {
        if (!enableInfo)
            return;

        logger.info(StringUtils.format(message, params));
    }

    public static void info(Logger logger, long startTime, long threshold, String message, Object... params) {
        if (!enableInfo)
            return;

        long costtime = isOnThreshold(startTime, threshold, message);
        if (costtime != -1) {
            message = formatCosttime(message, costtime);
            info(logger, message, params);
        }
    }

    public static void warn(Logger logger, String message, Object... params) {
        if (!enableWarn)
            return;

        logger.warn(StringUtils.format(message, params));
    }

    public static void warn(Logger logger, long startTime, long threshold, String message, Object... params) {
        if (!enableWarn)
            return;

        long costtime = isOnThreshold(startTime, threshold, message);
        if (costtime != -1) {
            message = formatCosttime(message, costtime);
            warn(logger, message, params);
        }
    }

    public static void error(Logger logger, String message, Object... params) {
        if (!enableError)
            return;
        increErrorCount(logger);

        logger.error(StringUtils.format(message, params));
    }

    /**
     * 增加错误的数量
     * 
     * @param logger
     */
    private static void increErrorCount(Logger logger) {
        String name = logger.getName();
        Counter existCount = null;
        if (null == (existCount = errorCounters.get(name))) {
            Counter counter = new Counter();
            existCount = errorCounters.putIfAbsent(name, counter);
            if (null == existCount) {
                existCount = counter;
            }
        }
        existCount.inc();
        errorCounter.inc();
    }

    public static void error(Logger logger, Throwable t, String message, Object... params) {
        if (!enableError)
            return;

        increErrorCount(logger);

        String info = StringUtils.format(message, params);
        if (t == null)
            logger.error(info);
        else
            logger.error(info, t);
    }

    private static long isOnThreshold(long startTime, long threshold, String message) {
        long costtime = System.currentTimeMillis() - startTime;
        return costtime >= threshold ? costtime : -1;
    }

    /**
     * "cost time {t}" will be replaced to "cost time 100"
     */
    public static String formatCosttime(String message, long costtime) {
        return message.replaceAll("\\{t(ime)?\\}", String.valueOf(costtime));
    }

    public static void main(String[] args) throws InterruptedException {
        LogUtils.debug(logger, "I am {0}, age: {1}", "Leon", 213.1);
        System.out.println(formatCosttime("cost time {time}", 100));
    }
}
