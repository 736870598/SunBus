# SunBus
一套高扩展性的通信框架，仿EventBus的核心原理


#### 注册事件：

    SunBus.getDefault().register(this);

#### 解注册：

     SunBus.getDefault().unRegister(this);

#### 可选方法：
##### 在register和unregister期间调用，相对register和unregister来说操作更少一点；在register中会调用onStart方法，但是在onStop之前多次调用onStart只会执行一次

     SunBus.getDefault().onStart(this);
     SunBus.getDefault().onStop(this);

#### 注册事件：

     * 参数说明：
     * value      订阅的标签 (string数组)
     * isOne      其他地方通过postWait方式发送事件时，如果接受该事件的方法刚好不在线或者执行了onStop（）后，
     *             该事件将会被缓存下来，在接受者上线的时候，从缓存中拿到该事件并传递给接受者，如果接受者设置了
     *             isOne为true的话，那么如果该标签有很多事件的话只会调用接受者一次。  （默认为false）
     * threadMode 设置接收事件所在的线程
     *             ThreadMode.MainThread  设置接收事件在主线程，
     *             ThreadMode.AsyncThread 设置接收事件在异步线程，
     *             ThreadMode.PostThread  设置接收事件在发送事件的线程；（默认为 ThreadMode.PostThread ）

    @Subscribe("post0")
    private void post(String str){
        Log.v("sunxy", "MainActivity收到事件 123456, str =" + str);
    }

    @Subscribe(value = {"post1", "post2"}, isOne = true)
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

#### 移除事件：

    /**
    * 移除事件 （可移除通过 postWait 发送还没有接收的事件）
    * @param label    事件标签
    * @param params   参数  如果不传则将缓存的该标签的所有事件全部移除，否则匹配参数相同才移除
    */
    SunBus.getDefault().removePost("post", "123");

#### 特别注意：

   * 事件标签只支持string类型的，事件方法可接受任意的参数。
   * 在kotlin中使用的话必须道义以下引用：

            compile 'org.jetbrains.kotlin:kotlin-reflect:xx.xx.xx'
   * 关于混淆:

          -keepclassmembers class ** {
              @com.sunxiaoyu.sunbus.core.Subscribe <methods>;
          }

#### 升级说明（ jar包保存在JAR文件夹下 ）：

    1.0.0 基础版本
    1.1.0 支持kotlin
    1.2.0 加入标识可以设置粘性事件只调用一次
    1.2.1 增加了可移除缓存中事件的方法
    1.2.2 增加了可以控制接收线程的方法






