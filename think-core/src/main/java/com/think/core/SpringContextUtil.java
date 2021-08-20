package com.think.core;

import com.think.core.bean.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @Date :2021/8/18
 * @Name :SpringContextUtil
 * @Description : 请输入
 */
@Component
public class SpringContextUtil {

    @Autowired
    private ApplicationContext applicationContext;

    public Object getBean(String name){
        return applicationContext.getBean(name);
    }

    public void registerBean(String beanName ,Object object){


        ConfigurableApplicationContext configurableApplicationContext =(ConfigurableApplicationContext) this.applicationContext;

        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();



        // 通过BeanDefinitionBuilder创建bean定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(object.getClass());


        // 设置属性userService,此属性引用已经定义的bean:userService,这里userService已经被spring容器管理了.
//        beanDefinitionBuilder.addPropertyReference("testService", "testService");



        // 注册bean
        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getRawBeanDefinition());

    }

}
