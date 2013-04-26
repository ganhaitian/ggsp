package com.gg.ggsp.pool;

import com.gg.ggsp.filter.stat.Filter;

import java.sql.Connection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-24
 * Time: 下午4:09
 * To change this template use File | Settings | File Templates.
 */
public abstract class GgspAbstractDataSource {

    public final static boolean                             DEFAULT_TEST_ON_BORROW                    = true;
    public final static String                              DEFAULT_VALIDATION_QUERY                  = null;
    public final static boolean                             DEFAULT_WHILE_IDLE                        = false;
    public final static long                                DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = -1L;

    protected volatile boolean                              defaultAutoCommit                         = true;

    protected volatile int                                  maxWaitThreadCount                        = -1;

    protected volatile long                                 timeBetweenEvictionRunsMillis             = DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

    protected final Map<GgspPooledConnection, Object>       activeConnection                          = new IdentityHashMap<GgspPooledConnection, Object>();
    protected final static Object                           PRESENT                                   = new Object();

    protected volatile Throwable                            createError;

    protected volatile boolean                              removeAbandoned;

    private volatile boolean                                testOnBorrow                              = DEFAULT_TEST_ON_BORROW;
    private volatile boolean                                testWhileIdle                             = DEFAULT_WHILE_IDLE;

    protected volatile ValidConnectionChecker               validConnectionChecker                    = null;

    protected volatile String                               validationQuery                           = DEFAULT_VALIDATION_QUERY;
    protected volatile int                                  validationQueryTimeout                    = -1;

    public final static int                                 DEFAULT_MAX_WAIT                          =-1;

    protected volatile long                                 maxWait                                   =DEFAULT_MAX_WAIT;

    protected List<Filter>                                  filters                                   =new CopyOnWriteArrayList<Filter>();

    protected ReentrantLock                                 lock;
    protected Condition                                     notEmpty;
    protected Condition                                     empty;

    public GgspAbstractDataSource(boolean lockFair){
        lock = new ReentrantLock(lockFair);

        notEmpty = lock.newCondition();
        empty = lock.newCondition();
    }

    public boolean isDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public ValidConnectionChecker getValidConnectionChecker() {
        return validConnectionChecker;
    }

    public void setValidConnectionChecker(ValidConnectionChecker validConnectionChecker) {
        this.validConnectionChecker = validConnectionChecker;
    }

    public int getValidationQueryTimeout() {
        return validationQueryTimeout;
    }

    public void setValidationQueryTimeout(int validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public int getMaxWaitThreadCount() {
        return maxWaitThreadCount;
    }

    public void setMaxWaitThreadCount(int maxWaitThreadCount) {
        this.maxWaitThreadCount = maxWaitThreadCount;
    }

    public abstract void discardConnection(Connection realConnection);
}
