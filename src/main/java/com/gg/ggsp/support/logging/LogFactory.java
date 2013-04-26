package com.gg.ggsp.support.logging;

import java.lang.reflect.Constructor;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-25
 * Time: 下午5:38
 * To change this template use File | Settings | File Templates.
 */
public final class LogFactory {

    private static Constructor logConstructor;

    static {
        tryImplementation("org.apache.log4j.Logger","com.gg.ggsp.support.logging.Log4jImpl");
    }

    public static void tryImplementation(String testClassName,String implClassName){
        if(logConstructor == null){
            try{
                Resources.classForName(testClassName);
                Class implClass = Resources.classForName(implClassName);
                logConstructor = implClass.getConstructor( new Class[]{ Class.class });
            }catch(Throwable t){

            }
        }
    }

    public static Log getLog (Class clazz){
        try{
            return (Log)logConstructor.newInstance(clazz);
        }catch(Throwable t){
            throw new RuntimeException("Error Creating logger for class " + clazz +". Cause :" + t,t);
        }
    }

}
