package com.gg.ggsp.stat;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-25
 * Time: 下午3:25
 * To change this template use File | Settings | File Templates.
 */
public final class JdbcSqlStat implements  JdbcSqlStatMBean{

    public final static String getContextSqlFile(){
        JdbcStatContext context=JdbcStatManager.getInstance().getStatContext();
        if(context==null){
            return null;
        }
        return context.getFile();
    }

    public final static String getContextSqlName(){
        JdbcStatContext context=JdbcStatManager.getInstance().getStatContext();
        if(context==null){
            return null;
        }
        return context.getSql();
    }

    public final static void setContextSqlName(String val){
        JdbcStatContext context=JdbcStatManager.getInstance().getStatContext();
        if(context == null){
            context=JdbcStatManager.getInstance().createStatContext();
            JdbcStatManager.getInstance().setStatContext(context);
        }

        context.setSql(val);
    }

    public final static void setContextSqlFile(String val){
        JdbcStatContext context=JdbcStatManager.getInstance().getStatContext();
        if(context == null){
            context=JdbcStatManager.getInstance().createStatContext();
            JdbcStatManager.getInstance().setStatContext(context);
        }

        context.setFile(val);
    }

}
