package com.gg.ggsp.support.logging;

/**
 * Created with IntelliJ IDEA.
 * User: ganhaitian
 * Date: 13-4-25
 * Time: 下午8:44
 * To change this template use File | Settings | File Templates.
 */
public final class Resources {

    private static ClassLoader defaultClassLoader;

    public static Class classForName(String clsName) throws ClassNotFoundException {
        Class<?> clazz=null;
        try{
            clazz=getClassLoader().loadClass(clsName);
        }catch(Exception e){

        }

        if(clazz == null){
            clazz = Class.forName(clsName);
        }

        return clazz;
    }

    private static ClassLoader getClassLoader(){
        if(defaultClassLoader == null){
            defaultClassLoader = Thread.currentThread().getContextClassLoader();
        }
        return defaultClassLoader;
    }

}
