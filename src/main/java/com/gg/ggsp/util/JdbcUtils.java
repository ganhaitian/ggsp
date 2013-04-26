package com.gg.ggsp.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-25
 * Time: 下午4:40
 * To change this template use File | Settings | File Templates.
 */
public final class JdbcUtils implements JdbcConstants{

       public final static void close(Statement x){
            if(x != null){
                try{
                    x.close();
                }catch(Exception e){

                }
            }
       }

    public final static void close(ResultSet x){
        if(x != null){
            try{
                x.close();
            }catch(Exception e){

            }
        }
    }

    public final static void close(Connection conn){
        if(conn != null){
            try{
                conn.close();
            }catch(Exception e){

            }
        }
    }
}
