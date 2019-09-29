package com.github.casside.util;

import java.lang.reflect.Method;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class ControllerUtil {

    /**
     * 去掉Controller的Mapping
     */
    public static void unregisterController(BeanFactory beanFactory, String controllerBeanName) {
        final RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping)
            beanFactory.getBean("requestMappingHandlerMapping");
        if (requestMappingHandlerMapping != null) {
            Object controller = beanFactory.getBean(controllerBeanName);
            if (controller == null) {
                return;
            }
            final Class<?> targetClass = controller.getClass();
            ReflectionUtils.doWithMethods(targetClass, method -> {
                Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
                try {
                    Method createMappingMethod = RequestMappingHandlerMapping.class.
                        getDeclaredMethod("getMappingForMethod", Method.class, Class.class);
                    createMappingMethod.setAccessible(true);
                    RequestMappingInfo requestMappingInfo = (RequestMappingInfo)
                        createMappingMethod.invoke(requestMappingHandlerMapping, specificMethod, targetClass);
                    if (requestMappingInfo != null) {
                        requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, ReflectionUtils.USER_DECLARED_METHODS);
        }
    }

    /**
     * 注册Controller
     */
    public static void registerController(BeanFactory beanFactory, String controllerBeanName) throws Exception {
        final RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping)
            beanFactory.getBean("requestMappingHandlerMapping");
        if (requestMappingHandlerMapping != null) {
            Object controller = beanFactory.getBean(controllerBeanName);
            if (controller == null) {
                return;
            }
            unregisterController(beanFactory, controllerBeanName);
            //注册Controller
            Method method = requestMappingHandlerMapping.getClass().getSuperclass().getSuperclass().
                getDeclaredMethod("detectHandlerMethods", Object.class);
            method.setAccessible(true);
            method.invoke(requestMappingHandlerMapping, controllerBeanName);
        }
    }

}
