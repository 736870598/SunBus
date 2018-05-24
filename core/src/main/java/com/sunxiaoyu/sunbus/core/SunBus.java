package com.sunxiaoyu.sunbus.core;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * -- 事件总线事件
 * <p>
 * Created by sunxy on 2018/4/24 0024.
 */

public class SunBus {

    /**
     * 方法注解集合（缓存使用）
     * key为class，value为该class下所有的 [订阅的标签、订阅者(函数)、(函数)参数]
     */
    private static final Map<Class<?>, List<SubscriberMethod>> METHOD_CACHE = new HashMap<>();

    /**
     * 订阅集合
     * 发送事件的时候 通过Key(标签)查找所有对应的订阅者
     * key 为 订阅的标签 , value为 [订阅者(函数所在的对象)、[订阅的标签、订阅者(函数)、(函数)参数]]
     */
    private static final Map<String, List<Subscription>> SUBSCRIBES = new  HashMap<>();

    /**
     * 对应对象中所有需要回调的标签 方便注销
     * key是订阅者(函数)所在类对象， value是该类中所有的订阅标签
     */
    private static final Map<Class<?>, List<String>> REGISTERS = new HashMap<>();

    /**
     * 缓存待发送的消息
     * key是订阅标签，value是传入的参数
     */
    private static final Map<String, List<Object[]>> POST_CACHE = new HashMap<>();

    /**
     * 保存订阅者的状态
     * value： true 该订阅者可以接受事件  false 订阅者不可见，postWait的时候不传递事件
     */
    private static final Map<Object, Boolean> ACTION_STATUS = new HashMap<>();

    private volatile static SunBus instance;
    private volatile ExecutorService executorService;
    private volatile Handler mainHandler;

    private SunBus(){ }

    public static SunBus getDefault(){
        if (instance == null){
            synchronized (SunBus.class){
                if (instance == null){
                    instance = new SunBus();
                }
            }
        }
        return instance;
    }

    private void initHandler(){
        if (mainHandler == null){
            synchronized (SunBus.class){
                if (mainHandler == null){
                    mainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
    }

    private void initExecutorService(){
        if (executorService == null){
            synchronized (SunBus.class){
                if (executorService == null){
                    executorService = Executors.newCachedThreadPool();
                }
            }
        }
    }

    /**
     * 注册方法
     *  调用该方法后会找到class中注册的方法，并且如果之前有带发送的事件，会在这个时候调用发送
     *  所以该方法最好放在 onCreate()的最后 或者 onStart() 方法中执行
     *
     *  将 subscriber 类中所有的被Subscribe标注的方法都找到并缓存下来
     *
     */
    public void register(Object subscriber){
        Class<?> subscriberClass = subscriber.getClass();
        //找到被Subscribe注解的函数 并记录缓存
        List<SubscriberMethod> subscriberMethods = findSubscribe(subscriberClass);

        //为了方便注销
        List<String> labels = REGISTERS.get(subscriberClass);
        if (labels == null){
            labels = new ArrayList<>();
        }

        //加入注册集合，方便post时候使用
        for (SubscriberMethod subscriberMethod : subscriberMethods) {
            String label = subscriberMethod.getLabel();
            if (!labels.contains(label)){
                //如果之前没有, 加入集合中
                labels.add(label);
            }
            List<Subscription> subscriptions = SUBSCRIBES.get(label);
            if (subscriptions == null){
                subscriptions = new ArrayList<>();
                SUBSCRIBES.put(label, subscriptions);
            }
            subscriptions.add(new Subscription(subscriber, subscriberMethod));

        }

        REGISTERS.put(subscriberClass, labels);
        onStart(subscriber);
    }

    /**
     * 从class中找到所有添加了
     * @param subscriberClass
     * @return
     */
    private List<SubscriberMethod> findSubscribe(Class<?> subscriberClass) {
        List<SubscriberMethod> subscriberMethods = METHOD_CACHE.get(subscriberClass);
        if (subscriberMethods == null){
            subscriberMethods = new ArrayList<>();
            Method[] methods = subscriberClass.getDeclaredMethods();
            for (Method method : methods) {
                Subscribe subscribeAnnotation = method.getAnnotation(Subscribe.class);
                if (subscribeAnnotation != null){
                    //获取注解上的标签
                    String[] values = subscribeAnnotation.value();
                    boolean isOne = subscribeAnnotation.isOne();
                    ThreadMode threadMode = subscribeAnnotation.threadMode();
                    //获取方法参数
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    for (String value : values) {
                        method.setAccessible(true);
                        //将标签，方法名，方法参数封装好保存到list中
                        SubscriberMethod subscriberMethod = new SubscriberMethod(value, method,
                                parameterTypes, isOne, threadMode);
                        subscriberMethods.add(subscriberMethod);
                    }
                }
            }
            //缓存
            METHOD_CACHE.put(subscriberClass, subscriberMethods);
        }
        return subscriberMethods;
    }


    /**
     * onStart 该方法一定要与onStop方法配套使用
     */
    public void onStart(Object subscriber){
        Boolean status = ACTION_STATUS.get(subscriber);
        if (status != null && status){
            //之前已经可见了，还可见个毛
            return;
        }

        ACTION_STATUS.put(subscriber, true);
        List<SubscriberMethod> subscriberMethods = METHOD_CACHE.get(subscriber.getClass());
        if (subscriberMethods != null && !subscriberMethods.isEmpty()){
            for (SubscriberMethod subscriberMethod : subscriberMethods) {
                //得到待发送的消息
                List<Object[]> objects = POST_CACHE.remove(subscriberMethod.getLabel());
                if (objects != null && !objects.isEmpty()){
                    for (Object[] object : objects) {
                        post(subscriber, subscriberMethod, object);
                        if (subscriberMethod.isOne()){
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * onStop 该方法一定要与onStart方法配套使用
     */
    public void onStop(Object subscriber){
        ACTION_STATUS.remove(subscriber);
    }

    /**
     * 注销方法
     */
    public void unRegister(Object subscriber){
        onStop(subscriber);
        List<String> labels = REGISTERS.remove(subscriber.getClass());
        if (labels != null){
            for (String label : labels) {
                List<Subscription> subscriptions = SUBSCRIBES.get(label);
                Iterator<Subscription> iterator = subscriptions.iterator();
                while (iterator.hasNext()){
                    Subscription next = iterator.next();
                    if (next.getSubscriber() == subscriber){
                        iterator.remove();
                    }
                }
            }
        }
    }

    /**
     * 发送事件 (没有接受收者时，该事件为无效发送)
     * @param label   标签
     * @param params  参数集合
     */
    public void post(String label, Object...params){
        List<Subscription> subscriptions = SUBSCRIBES.get(label);
        if(subscriptions != null && !subscriptions.isEmpty()){
            for (Subscription subscription : subscriptions) {
                Boolean aBoolean = ACTION_STATUS.get(subscription.getSubscriber());
                if (aBoolean != null && aBoolean){
                    post(subscription.getSubscriber(), subscription.getSubscriberMethod(), params);
                }
            }
        }
    }

    /**
     * 发送事件  （有接受者时直接发送，没有接收者时先保存起来，等有接受者时再进行发送）
     * 使用该方法可能会造成内存泄漏，请谨慎使用
     * @param label     标签
     * @param params    参数集合
     */
    public void postWait(String label, Object...params){
        List<Subscription> subscriptions = SUBSCRIBES.get(label);
        if(subscriptions != null && !subscriptions.isEmpty()){
            boolean isExe = false;
            for (Subscription subscription : subscriptions) {
                Boolean aBoolean = ACTION_STATUS.get(subscription.getSubscriber());
                if (aBoolean != null && aBoolean){
                    post(subscription.getSubscriber(), subscription.getSubscriberMethod(), params);
                    isExe = true;
                }
            }
            if (!isExe){
                //如果没有执行。说明activity可能不可见，将事件保存下来
                savePost(label, params);
            }
        }else{
            //没有接受者接收该label消息，先缓存取来，等有接受者是再发送出去
            savePost(label, params);
        }
    }

    /**
     * 缓存待发送事件，等有接受者时候再发送出去
     */
    private void savePost(String label, Object...params){
        List<Object[]> objects = POST_CACHE.get(label);
        if (objects == null){
            objects = new ArrayList<>();
            POST_CACHE.put(label, objects);
        }
        objects.add(params);
    }

    /**
     * 移除事件 （可移除通过 postWait 发送还没有接收的事件）
     * @param label    事件标签
     * @param params   参数  如果不传则将缓存的该标签的所有事件全部移除，否则匹配参数相同才移除
     */
    public void removePost(String label, Object...params){
        if (params == null){
            POST_CACHE.remove(label);
        }else{
            List<Object[]> objects = POST_CACHE.get(label);
            if (objects != null){
                Iterator<Object[]> iterator = objects.iterator();
                while (iterator.hasNext()){
                    Object[] object = iterator.next();
                    if (params == object){
                        iterator.remove();
                    }
                }
            }
        }
    }


    /**
     * 发送事件
     * @param subscriber  object
     * @param method      方法名
     * @param params      参数
     */
    private void post(final Object subscriber, final SubscriberMethod method, final Object...params){
        switch (method.getThreadMode()){
            case PostThread:
                sendMethod(subscriber, method, params);
                break;
            case MainThread:
                if (Looper.getMainLooper() == Looper.myLooper()){
                    //当前就是主线程
                    sendMethod(subscriber, method, params);
                }else{
                    //当前是非主线程，
                    initHandler();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            sendMethod(subscriber, method, params);
                        }
                    });
                }
                break;
            case AsyncThread:
                if (Looper.getMainLooper() == Looper.myLooper()){
                    //当前就是主线程
                    initExecutorService();
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            sendMethod(subscriber, method, params);
                        }
                    });

                }else{
                    //当前是非主线程，
                    sendMethod(subscriber, method, params);
                }
                break;
            default:
                break;
        }

    }

    private void sendMethod(Object subscriber, SubscriberMethod method, Object...params){
        //组装参数
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] invokeParams = new Object[parameterTypes.length];
        for (int i = 0; i < invokeParams.length; i++) {
            if (params != null && i < params.length && parameterTypes[i].isInstance(params[i])){
                invokeParams[i] = params[i];
            }else{
                invokeParams[i] = null;
            }
        }
        //进行反射调用
        try {
            method.getMethod().invoke(subscriber, invokeParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
