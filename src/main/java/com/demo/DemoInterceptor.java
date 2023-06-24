package com.demo;

import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.network.trace.component.ComponentsDefine;

import java.lang.reflect.Method;

public class DemoInterceptor implements InstanceMethodsAroundInterceptor {
    @Override
    public void beforeMethod(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, MethodInterceptResult methodInterceptResult) {
        AbstractSpan span = ContextManager.createLocalSpan("swThread/" + method.getDeclaringClass().getSimpleName() + "." + method.getName());
        span.setComponent(ComponentsDefine.GSON);
    }

    @Override
    public Object afterMethod(EnhancedInstance enhancedInstance, Method method, Object[] allArguments, Class<?>[] classes, Object ret) {
        if (!ContextManager.isActive()) {
            return ret;
        } else {
            if (allArguments != null && allArguments.length > 0) {
                ContextManager.activeSpan().tag(Tags.ofKey("sleepTime"), allArguments[0].toString());
            }
            ContextManager.activeSpan().tag(Tags.ofKey("test"), "12345");
            ContextManager.stopSpan();
            return ret;
        }
    }

    @Override
    public void handleMethodException(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, Throwable t) {
        ContextManager.activeSpan().log(t);
    }
}
