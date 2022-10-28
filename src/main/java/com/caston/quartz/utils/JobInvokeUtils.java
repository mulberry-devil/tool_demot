package com.caston.quartz.utils;

import com.caston.quartz.entity.Task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JobInvokeUtils {
    public static void invokeMethod(Task task) throws Exception {
        String beanClass = task.getBeanClass();
        if (beanClass != null && beanClass.length() != 0) {
            String beanName = BeansUtils.getBeanName(beanClass);
            String methodName = BeansUtils.getMethodName(beanClass);
            Object bean = Class.forName(beanName).newInstance();
            invokeMethod(bean, methodName);
        }
    }

    public static void invokeMethod(Object bean, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = bean.getClass().getDeclaredMethod(methodName);
        method.invoke(bean);
    }
}
