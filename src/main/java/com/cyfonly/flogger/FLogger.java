package com.cyfonly.flogger;

import com.cyfonly.flogger.constants.Constant;
import com.cyfonly.flogger.strategy.LogManager;
import com.cyfonly.flogger.utils.CommUtil;
import com.cyfonly.flogger.utils.TimeUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 日志工具类,LogManager会启动线程，进程不会自动终止
 *
 * 日志不会自动清理
 *
 * 只有error和fetal日志会打印到控制台
 *
 * @author yunfeng.cheng
 * @version 2015/10/31
 */
public class FLogger {

    private static FLogger instance;
    private static LogManager logManager;

    static {
        logManager = LogManager.getInstance();
    }

    private FLogger() {
    }

    public static synchronized FLogger getInstance() {
        if (instance == null) {
            instance = new FLogger();
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
            sb.append("[");
            sb.append(Constant.LOG_DESC_MAP.get(String.valueOf(level)));
            sb.append("] ");
            sb.append(TimeUtil.getFullDateTime());
            sb.append(" [");
            sb.append(Thread.currentThread().getName());
            sb.append("] ");
            sb.append(logMsg);
            sb.append("\n");
            logManager.addLog(logFileName, sb);

            //错误信息同时打印到控制台
            if (Constant.CONSOLE_PRINT) {
                try {
                    System.out.print(new String(sb.toString().getBytes(Constant.CFG_CHARSET_NAME), Constant.CFG_CHARSET_NAME));
                } catch (Exception e) {
                    System.out.print(CommUtil.getExpStack(e));
                }
            }
        }
    }

}
