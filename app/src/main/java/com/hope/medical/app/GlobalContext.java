package com.hope.medical.app;

import android.app.Application;


import cn.jpush.android.api.JPushInterface;

/**
 * Created by wutao on 2016/6/21.
 */

public class GlobalContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

       /* Bugly SDK初始化
        * 参数1：上下文对象
        * 参数2：APPID，平台注册时得到,注意替换成你的appId
        * 参数3：是否开启调试模式，调试模式下会输出'CrashReport'tag的日志
        */

        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);
    }
}
