package com.grgbanking.baselib.util;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author Bear
 *  做一个方法，可以将一个JavaBean风格对象的属性值拷贝到另一个对象的同名属性中 (如果不存在同名属性的就不拷贝）
**/

public class BeanCopyUtil {

    @SuppressWarnings("unchecked")
    public static void copy(Object target, Object source) throws Exception {

        /*
         * 分别获得源对象和目标对象的Class类型对象,Class对象是整个反射机制的源头和灵魂！
         * 
         * Class对象是在类加载的时候产生,保存着类的相关属性，构造器，方法等信息
         */

        Class sourceClz = source.getClass();

        Class targetClz = target.getClass();

        // 得到Class对象所表征的类的所有属性(包括私有属性)

        Field[] fields = sourceClz.getDeclaredFields();

        if (fields.length == 0) {
            fields = sourceClz.getSuperclass().getDeclaredFields();
        }
        for (int i = 0; i < fields.length; i++) {

            String fieldName = fields[i].getName();

            Field targetField = null;

            // 得到targetClz对象所表征的类的名为fieldName的属性，不存在就进入下次循环

            try {
                targetField = targetClz.getDeclaredField(fieldName);

            } catch (NoSuchFieldException e) {
                targetField = targetClz.getSuperclass().getDeclaredField(fieldName);
            }
            // 判断sourceClz字段类型和targetClz同名字段类型是否相同

            if (fields[i].getType() == targetField.getType()) {

                // 由属性名字得到对应get和set方法的名字

                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                // 由方法的名字得到get和set方法的Method对象

                Method getMethod;
                Method setMethod;
                try {

                    try {
                        getMethod = sourceClz.getDeclaredMethod(getMethodName, new Class[] {});

                    } catch (NoSuchMethodException e) {
                        getMethod = sourceClz.getSuperclass().getDeclaredMethod(getMethodName, new Class[] {});
                    }

                    try {
                        setMethod = targetClz.getDeclaredMethod(setMethodName, fields[i].getType());

                    } catch (NoSuchMethodException e) {
                        setMethod = targetClz.getSuperclass().getDeclaredMethod(setMethodName, fields[i].getType());
                    }

                    // 调用source对象的getMethod方法

                    Object result = getMethod.invoke(source, new Object[] {});

                    // 调用target对象的setMethod方法

                    setMethod.invoke(target, result);

                } catch (SecurityException e) {
                    e.printStackTrace();

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();

                } catch (IllegalAccessException e) {
                    e.printStackTrace();

                } catch (InvocationTargetException e) {
                    e.printStackTrace();

                }

            } else {

                throw new Exception("同名属性类型不匹配！");

            }

        }

    }

    /**
     * @Title: 对象数据类型复制： ObjA coryTo ObjB
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param target  =  ObjB.class
     * @param source   =  原数据对象(ObjA)
     * @return 
     */ 
    public static <T> T copy(Class<T> target, Object source) {
        try {
            Gson gson = new Gson();
            String result = gson.toJson(source);
            return gson.fromJson(result, target);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * @Title: 集合数据类型复制： List<clsA> coryTo List<clsB>
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param typeOfT  =  new TypeToken<List<clsB>() {}.getType()
     * @param source   =  原数据对象: List<clsA>
     * @return 
     */ 
    public static <T> T copy(Type typeOfT, Object source) {

        try {
            Gson gson = new Gson();
            String result = gson.toJson(source);
            return gson.fromJson(result, typeOfT);
        } catch (Exception e) {
        }
        return null;
    }

}
