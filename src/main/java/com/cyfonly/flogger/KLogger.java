package com.cyfonly.flogger;

import com.cyfonly.flogger.constants.Constant;
import com.cyfonly.flogger.strategy.LogManager;
import com.cyfonly.flogger.utils.CommUtil;
import com.cyfonly.flogger.utils.TimeUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志工具类,LogManager会启动线程，进程不会自动终止
 * <p>
 * 日志不会自动清理
 *
 * 日志都会在控制台打印一份
 *
 * @author yunfeng.cheng
 * @version 2015/10/31
 */
public class KLogger {

    private static KLogger instance;
    private static LogManager logManager;

    static {
        logManager = LogManager.getInstance();
    }

    private KLogger() {
    }

    public static synchronized KLogger getInstance() {
        if (instance == null) {
            instance = new KLogger();
        }
        return instance;
    }

    /**
     * 写调试日志
     *
     * @param logMsg 日志内容
     */
    public void debug(String logMsg) {
        writeLog("debug", Constant.DEBUG, logMsg);
    }

    /**
     * 写普通日志
     *
     * @param logMsg 日志内容
     */
    public void info(String logMsg) {
        writeLog("info", Constant.INFO, logMsg);
    }

    /**
     * 写警告日志
     *
     * @param logMsg 日志内容
     */
    public void warn(String logMsg) {
        writeLog("warn", Constant.WARN, logMsg);
    }

    /**
     * 写错误日志
     *
     * @param logMsg 日志内容
     */
    public void error(String logMsg) {
        writeLog("error", Constant.ERROR, logMsg);
    }

    /**
     * 写错误日志
     *
     * @param logMsg 日志内容
     */
    public void error(String logMsg, Throwable t) {
        writeLog("error", Constant.ERROR, logMsg);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        writeLog("error", Constant.ERROR, sw.toString());
    }

    /**
     * 写严重错误日志
     *
     * @param logMsg 日志内容
     */
    public void fatal(String logMsg) {
        writeLog("fatal", Constant.FATAL, logMsg);
    }

    /**
     * 写系统日志
     *
     * @param level  日志级别
     * @param logMsg 日志内容
     */
    public void writeLog(int level, String logMsg) {
        writeLog(Constant.LOG_DESC_MAP.get(String.valueOf(level)).toLowerCase(), level, logMsg);
    }

    /**
     * 写日志
     *
     * @param logFileName 日志文件名
     * @param level       日志级别
     * @param logMsg      日志内容
     */
    public void writeLog(String logFileName, int level, String logMsg) {
        if (logMsg != null && Constant.CFG_LOG_LEVEL.indexOf("" + level) >= 0) {
            StringBuffer sb = new StringBuffer(logMsg.length() + 100);
            sb.append(TimeUtil.getFullDateTime());

            sb.append(" ");
            sb.append(Constant.LOG_DESC_MAP.get(String.valueOf(level)));
            sb.append(" ");

            sb.append(getFileNameAndLineNumber());

            sb.append(" - ");

            sb.append(logMsg);
            sb.append("\n");
            logManager.addLog(logFileName, sb);

            //错误信息同时打印到控制台
            try {
                System.out.print(new String(sb.toString().getBytes(Constant.CFG_CHARSET_NAME), Constant.CFG_CHARSET_NAME));
            } catch (Exception e) {
                System.out.println(CommUtil.getExpStack(e));
            }
        }
    }

    private String getFileNameAndLineNumber() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        new Throwable().printStackTrace(pw);

        String clazName = this.getClass().getName();
        String[] lines = sw.toString().split("\n");

        int idx = -1;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.contains(clazName)) {
                idx = i;
            }
        }
        // info 样例： at com.kk.log4j.Log4jTest.main(Log4jTest.java:30)
        String info = lines[idx + 1];
        Pattern pattern = Pattern.compile("\\((.+)\\.java:(\\d+)\\)");
        Matcher matcher = pattern.matcher(info);
        if (matcher.find()) {
            String fileName = matcher.group(1);
            String lineNumber = matcher.group(2);
            return genString(fileName, ":", lineNumber);
        }
        return "";
    }

    private String genString(String... strs) {
        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            sb.append(str);
        }
        return sb.toString();
    }

}
