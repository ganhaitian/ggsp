package com.gg.ggsp.pool;

import java.sql.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-25
 * Time: 下午4:18
 * To change this template use File | Settings | File Templates.
 */
public interface ValidConnectionChecker {

    boolean isValidConnection(Connection conn, String validationQuery, int validationQueryTimeout);

}
