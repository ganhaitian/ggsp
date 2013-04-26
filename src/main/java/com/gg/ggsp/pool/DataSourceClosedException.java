package com.gg.ggsp.pool;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-26
 * Time: 下午2:47
 * To change this template use File | Settings | File Templates.
 */
public class DataSourceClosedException extends SQLException{

    public DataSourceClosedException(){
    }

    public DataSourceClosedException(String msg){
        super(msg);
    }

    public DataSourceClosedException(Throwable cause){
        super(cause);
    }
}
