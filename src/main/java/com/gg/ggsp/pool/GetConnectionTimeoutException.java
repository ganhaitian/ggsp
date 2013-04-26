package com.gg.ggsp.pool;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-26
 * Time: 下午5:42
 * To change this template use File | Settings | File Templates.
 */
public class GetConnectionTimeoutException extends SQLException{

    public GetConnectionTimeoutException(){

    }

    public GetConnectionTimeoutException(Throwable t){
        super(t);
    }

}
