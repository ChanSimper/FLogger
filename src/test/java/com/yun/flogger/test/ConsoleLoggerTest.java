package com.yun.flogger.test;

import com.cyfonly.flogger.ConsoleLogger;

/**
 * @auther zhihui.kzh
 * @create 24/1/17PM6:59
 */
public class ConsoleLoggerTest {
    private static ConsoleLogger logger = new ConsoleLogger();

    public static void main(String[] args) {
        logger.debug("xx"); // 2017-01-24 19:25:50.796 DEBUG ConsoleLoggerTest:13 - xx
        logger.info("xx");
        logger.warn("xx");
        logger.error("xx");
        logger.error("xx",new Exception("error!!"));
    }
}
