package com.caston.quartz.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class BeansUtils implements BeanFactoryPostProcessor {
    private static ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeansUtils.beanFactory = beanFactory;
    }

    public static <T> T getBean(Class<T> cla) throws BeansException {
        return beanFactory.getBean(cla);
    }

    /**
     * 获取bean名称.
     *
     * @param beanClass 目标字符串
     * @return bean名称
     */
    public static String getBeanName(String beanClass) {
        int index = beanClass.lastIndexOf(".");
        return index == -1 ? beanClass : beanClass.substring(0, index);
    }

    /**
     * 获取bean方法.
     *
     * @param beanClass 目标字符串
     * @return method方法
     */
    public static String getMethodName(String beanClass) {
        int endIndex = beanClass.indexOf("(");
        int startIndex = beanClass.lastIndexOf(".");
        if (startIndex == -1) {
            return null;
        }
        return beanClass.substring(startIndex + 1, endIndex);
    }
}
