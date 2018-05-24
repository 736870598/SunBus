package com.sunxiaoyu.sunbus.core;

import java.lang.reflect.Method;

/**
 * --
 * <p>
 * Created by sunxy on 2018/4/24 0024.
 */

public class SubscriberMethod {

    private String label;
    private Method method;
    private Class<?>[] parameterTypes;
    private boolean isOne;
    private ThreadMode threadMode;

    public SubscriberMethod(String label, Method method,
                            Class<?>[] parameterTypes, boolean isOne, ThreadMode threadMode) {
        this.label = label;
        this.method = method;
        this.parameterTypes = parameterTypes;
        this.isOne = isOne;
        this.threadMode = threadMode;
    }

    public String getLabel() {
        return label;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public boolean isOne() {
        return isOne;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }
}
