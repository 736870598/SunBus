package com.sunxiaoyu.sunbus.core;

/**
 * --
 * <p>
 * Created by sunxy on 2018/4/24 0024.
 */

public class Subscription {

    private Object subscriber;
    private SubscriberMethod subscriberMethod;

    public Subscription(Object subscriber, SubscriberMethod subscriberMethod) {
        this.subscriber = subscriber;
        this.subscriberMethod = subscriberMethod;
    }

    public Object getSubscriber() {
        return subscriber;
    }

    public SubscriberMethod getSubscriberMethod() {
        return subscriberMethod;
    }

}
