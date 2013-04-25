package com.gg.ggsp.stat;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-25
 * Time: 下午3:33
 * To change this template use File | Settings | File Templates.
 */
public class JdbcStatContext {

    private String file;
    private String sql;

    public JdbcStatContext(){

    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
