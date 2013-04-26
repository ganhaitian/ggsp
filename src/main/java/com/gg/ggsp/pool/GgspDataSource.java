package com.gg.ggsp.pool;

import com.gg.ggsp.filter.stat.Filter;
import com.gg.ggsp.stat.JdbcSqlStat;
import com.gg.ggsp.support.logging.LogFactory;
import com.gg.ggsp.util.JdbcUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-24
 * Time: 下午4:07
 * To change this template use File | Settings | File Templates.
 */
public class GgspDataSource extends GgspAbstractDataSource{

    private final static Log                   LOG                     = LogFactory.getLog(GgspDataSource.class);

    private int                                activeCount             = 0;
    private int                                discardCount            = 0;



    public GgspDataSource(){
        super(false);
    }

    public GgspPooledConnection getConnection() throws SQLException {
        return getConnection(maxWait);
    }

    public GgspPooledConnection getConnection(long maxWait) throws SQLException{
        if(!filters.isEmpty()){
            return null;
        }else{
            return getConnectionDirect(maxWait);
        }
    }

    public GgspPooledConnection getConnectionDirect(long maxWait) throws SQLException{
         for(;;){
             GgspPooledConnection poolableConnection=getConnectionInternal(maxWait);

             if(isTestOnBorrow()){
                boolean validate=testConnectionInternal(poolableConnection.getConnection());
                if(!validate){
                    Connection realConnection=poolableConnection.getConnection();
                    //丢弃是因为检测到底层物理链接坏掉了吗？
                    discardConnection(realConnection);
                    continue;
                }
             }else{
                 Connection realConnection = poolableConnection.getConnection();
                 if(realConnection.isClosed()){
                     //这里的空丢弃是为了维护activeCount和discardCount这两个参数。
                     discardConnection(null);
                     continue;
                 }

                 if(isTestWhileIdle()){
                     long idleMills=System.currentTimeMillis()
                                    -poolableConnection.getConnectionHolder().getLastActiveTimeMillis();
                     if(idleMills >= this.getTimeBetweenEvictionRunsMillis()) {
                         boolean validate = testConnectionInternal(poolableConnection.getConnection());
                         if(!validate){
                            discardConnection(realConnection);
                             continue;
                         }
                     }

                 }
             }

             if(isRemoveAbandoned()){
                  StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                 poolableConnection.setConnectStackTrace(stackTrace);
                 poolableConnection.setConnectedTimeNano();
                 poolableConnection.setTraceEnable(true);

                 synchronized (activeConnection){
                      activeConnection.put(poolableConnection, PRESENT);
                 }
             }

             if(!this.isDefaultAutoCommit()){
                 poolableConnection.setAutoCommit(false);
             }

             return poolableConnection;
         }
    }


    public void discardConnection(Connection pooledConnection){
        JdbcUtils.close(pooledConnection);

        lock.lock();
        try{
            activeCount--;
            discardCount++;

            if(activeCount <= 0){
                empty.signal();
            }
        }finally {
            lock.unlock();
        }
    }

    protected boolean testConnectionInternal(Connection conn){
        String sqlFile= JdbcSqlStat.getContextSqlFile();
        String sqlName=JdbcSqlStat.getContextSqlName();

        if(sqlFile != null){
            JdbcSqlStat.setContextSqlFile(null);
        }

        if(sqlName != null){
            JdbcSqlStat.setContextSqlName(null);
        }

        try{
            if(validConnectionChecker != null){
                return validConnectionChecker.isValidConnection(conn, validationQuery, validationQueryTimeout);
            }

            if(conn.isClosed()){
                return false;
            }

            if(validationQuery == null){
                return true;
            }

            Statement st=null;
            ResultSet rs=null;
            try{
                st=conn.createStatement();
                if(getValidationQueryTimeout() > 0){
                    st.setQueryTimeout(validationQueryTimeout);
                }
                rs = st.executeQuery(validationQuery);
                if(!rs.next()){
                    return false;
                }
            }finally{
                JdbcUtils.close(st);
                JdbcUtils.close(rs);
            }

            return true;
        }catch(Exception e){
            return false;
        }finally{
            if(sqlFile != null){
                JdbcSqlStat.setContextSqlFile(sqlFile);
            }
            if(sqlName != null){
                JdbcSqlStat.setContextSqlName(sqlName);
            }

        }

    }

    private GgspPooledConnection getConnectionInternal(long maxWait) {
        return null;
    }

}
