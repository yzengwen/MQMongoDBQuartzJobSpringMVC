package com.sap.datahubmonitor.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  
  
/** 
 * Util class
 *  
 */  
public class ReflectionUtils {  
  
    private static Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);  
  
    /** 
     * using reflection to get the <T> type of the super class
     * if no definition, return Object.class
     * eg. 
     * public UserDao extends HibernateDao<User> 
     * 
     * @param clazz The class to introspect 
     * @return the first generic declaration, or Object.class if cannot be determined 
     */  
    @SuppressWarnings({ "unchecked", "rawtypes" })  
    public static <T> Class<T> getSuperClassGenricType(final Class clazz) {  
        return getSuperClassGenricType(clazz, 0);  
    }  
  
    /** 
     * using reflection to get the <T,T> type of the super class by index
     * if no definition, return Object.class
     * eg. 
     * public UserDao extends HibernateDao<User,Long> 
     * 
     * @param clazz clazz The class to introspect 
     * @param index the Index of the generic ddeclaration,start from 0. 
     * @return the index generic declaration, or Object.class if cannot be determined 
     */  
    @SuppressWarnings("rawtypes")  
    public static Class getSuperClassGenricType(final Class clazz, final int index) {  
  
        Type genType = clazz.getGenericSuperclass();  
  
        if (!(genType instanceof ParameterizedType)) {  
            logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");  
            return Object.class;  
        }  
  
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();  
  
        if (index >= params.length || index < 0) {  
            logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "  
                    + params.length);  
            return Object.class;  
        }  
        if (!(params[index] instanceof Class)) {  
            logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");  
            return Object.class;  
        }  
  
        return (Class) params[index];  
    }  
  
    
}