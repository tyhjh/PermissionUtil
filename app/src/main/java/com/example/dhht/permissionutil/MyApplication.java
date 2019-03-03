package com.example.dhht.permissionutil;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * @author tyhj
 * @date 2019/3/3
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
