package com.gg.ggsp.pool;

import com.gg.ggsp.filter.stat.Filter;
import com.gg.ggsp.stat.JdbcSqlStat;
import com.gg.ggsp.util.JdbcUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-24
 * Time: 下午4:07
 * To change this template use File | Settings | File Templates.
 */
public class GgspDataSource extends GgspAbstractDataSource{

    private final static Log                                LOG                                    = LogFactory.getLog(GgspDataSource.class);

    public final static int                                 DEFAULT_MAX_WAIT                        =-1;

    protected volatile long                                 maxWait                                 =DEFAULT_MAX_WAIT;

    protected List<Filter>                                  filters                                 =new CopyOnWriteArrayList<Filter>();

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

             }
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
