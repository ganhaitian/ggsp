package com.gg.ggsp.pool;

import com.gg.ggsp.filter.stat.Filter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-24
 * Time: 下午4:07
 * To change this template use File | Settings | File Templates.
 */
public class GgspDataSource {

    public final static int                                 DEFAULT_MAX_WAIT                        =-1;

    protected volatile long                                 maxWait                                 =DEFAULT_MAX_WAIT;

    protected List<Filter>                                   filters                               =new CopyOnWriteArrayList<Filter>();

    public GgspPooledConnection getConnection(){
        return getConnection(maxWait);
    }

    public GgspPooledConnection getConnection(long maxWait){
        if(!filters.isEmpty()){
            return null;
        }else{
            return getConnectionDirect(maxWait);
        }
    }

    private GgspPooledConnection getConnectionDirect(long maxWait) {

    }


}
