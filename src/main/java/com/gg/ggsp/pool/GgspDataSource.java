package com.gg.ggsp.pool;

import com.gg.ggsp.filter.stat.Filter;
import com.gg.ggsp.stat.JdbcSqlStat;
import com.gg.ggsp.support.logging.LogFactory;
import com.gg.ggsp.util.JdbcUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
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

    private long                               connectCount            = 0L;

    private int                                activeCount             = 0;
    private int                                poolingCount            = 0;
    private int                                discardCount            = 0;
    private int                                notEmptyWaitCount       = 0;
    private int                                notEmptyWaitThreadCount = 0;
    private int                                notEmptyWaitThreadPeak  = 0;
    private int                                notEmptySignalCount     = 0;

    private final AtomicLong                   connectErrorCount       = new AtomicLong();

    private volatile boolean                   enable                  = true;

    private volatile boolean                   closed                  = false;
    private long                               closeTimeMills          = -1L;

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

    private GgspPooledConnection getConnectionInternal(long maxWait) throws SQLException{
        if(closed){
            connectErrorCount.incrementAndGet();
            throw new DataSourceClosedException("DataSource already closed at "+new Date(closeTimeMills));
        }

        if(!enable){
            connectErrorCount.incrementAndGet();
            throw new DataSourceDisableException();
        }

        final long nanos = TimeUnit.MICROSECONDS.toNanos(maxWait);
        final int maxWaitThreadCount = getMaxWaitThreadCount();

        GgspConnectionHolder holder;

        try{
            lock.lockInterruptibly();
        }catch(InterruptedException e){
            connectErrorCount.incrementAndGet();
            throw new SQLException("interrupt",e);
        }

        try{
            if(maxWaitThreadCount > 0){
                if(notEmptyWaitThreadCount >= maxWaitThreadCount){
                    connectErrorCount.incrementAndGet();
                    throw new SQLException("maxWaitThreadCount " + maxWaitThreadCount + ", current wait Thread count"
                                           + lock.getQueueLength());
                }
            }

            connectCount ++;

            if(maxWait > 0){
                holder = pollLast(nanos);
            }else{
                holder = takeLast();
            }

        } catch (InterruptedException e) {

        } finally{

        }



        activeCount++;

        return null;
    }

    GgspConnectionHolder pollLast(long nano) throws InterruptedException, SQLException {
        long estimate = nano;

        for(int i = 0;; ++i){
            if(poolingCount == 0){
               empty.signal();

               if(estimate <=0 ){
                    if(this.createError == null ){
                        throw new GetConnectionTimeoutException();
                    }else{
                        throw new GetConnectionTimeoutException(createError);
                    }
               }

               notEmptyWaitThreadCount ++;
               if(notEmptyWaitThreadCount > notEmptyWaitThreadPeak ){
                   notEmptyWaitThreadPeak = notEmptyWaitThreadCount;
               }

                try{
                    long startEstimate = estimate;
                    estimate = notEmpty.awaitNanos(estimate);

                    notEmptyWaitCount ++;
                    notEmptyWaitNanos += (startEstimate - estimate );



                }catch(InterruptedException is){
                    notEmpty.signal();
                    notEmptySignalCount ++;
                    throw is;
                }finally {
                    notEmptyWaitThreadCount --;
                }
            }
        }

        return null;
    }

}
