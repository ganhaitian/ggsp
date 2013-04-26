package com.gg.ggsp.support.logging;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-25
 * Time: 下午8:37
 * To change this template use File | Settings | File Templates.
 */
public interface Log {

    boolean isDebugEnabled();

    void error(String msg, Throwable e);

    void error(String msg);

    boolean isInfoEnabled();

    void info(String msg);

    void debug(String msg);

    void debug(String msg, Throwable e);

    boolean isWarnEnabled();

    void warn(String msg);

    void warn(String msg, Throwable e);

    int getErrorCount();

    int getWarnCount();

    int getInfoCount();

    void resetStat();

}
