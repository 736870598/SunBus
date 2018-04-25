# SunBus
一套高扩展性的通信框架，仿EventBus的核心原理


#### 注册事件：

    SunBus.getDefault().register(this);

#### 解注册：

     SunBus.getDefault().unRegister(this);

#### 可选方法：
##### 在register中会调用onStart方法，但是在onStop之前多次调用onStart只会执行一次，这俩个方法完全可以用register和unregister代替，但是相对来说操作更少一点

     SunBus.getDefault().onStart(this);
     SunBus.getDefault().onStop(this);

#### 注册事件：

    @Subscribe("123456")
    private void post(String str){
        Log.v("sunxy", "MainActivity收到事件 123456, str =" + str);
    }

#### 发送事件：

     /**
     * 发送事件 (没有接受收者时，该事件为无效发送)
     * @param label   标签
     * @param params  参数集合
     */
    SunBus.getDefault().post("1234", "MainActivity发的事件");

     /**
     * 发送事件  （有接受者时直接发送，没有接收者时先保存起来，等有接受者时再进行发送）
     * 使用该方法可能会造成内存泄漏，请谨慎使用
     * @param label     标签
     * @param params    参数集合
     */
    SunBus.getDefault().postWait("1234", "MainActivity发的事件");




