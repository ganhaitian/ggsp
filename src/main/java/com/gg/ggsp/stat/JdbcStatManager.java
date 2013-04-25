package com.gg.ggsp.stat;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-25
 * Time: 下午3:30
 * To change this template use File | Settings | File Templates.
 */
public class JdbcStatManager {

    private static final JdbcStatManager                     instance        = new JdbcStatManager();

    public final ThreadLocal<JdbcStatContext>                contextLocal    = new ThreadLocal<JdbcStatContext>();

    public static final JdbcStatManager getInstance(){
        return instance;
    }

    public JdbcStatContext getStatContext(){
        return contextLocal.get();
    }

    public void setStatContext(JdbcStatContext context){
       contextLocal.set(context);
    }

    public JdbcStatContext createStatContext(){
        JdbcStatContext context = new JdbcStatContext();

        return context;
    }
}
