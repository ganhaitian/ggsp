package com.gg.ggsp.pool;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-24
 * Time: 下午4:09
 * To change this template use File | Settings | File Templates.
 */
public abstract class GgspAbstractDataSource {

    public final static boolean                             DEFAULT_TEST_ON_BORROW                   = true;
    public final static String                              DEFAULT_VALIDATION_QUERY                 = null;

    private volatile boolean                                testOnBorrow                            = DEFAULT_TEST_ON_BORROW;

    protected volatile ValidConnectionChecker               validConnectionChecker                  = null;

    protected volatile String                               validationQuery                         = DEFAULT_VALIDATION_QUERY;
    protected volatile int                                  validationQueryTimeout                  = -1;

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
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
}
