package com.gg.ggsp.pool;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-26
 * Time: 下午1:56
 * To change this template use File | Settings | File Templates.
 */
public class GgspConnectionHolder {

    private transient long                         lastActiveTimeMillis;

    public long getLastActiveTimeMillis() {
        return lastActiveTimeMillis;
    }

    public void setLastActiveTimeMillis(long lastActiveTimeMills) {
        this.lastActiveTimeMillis = lastActiveTimeMills;
    }

}
