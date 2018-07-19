package com.dji.uxsdkdemo;

/**
 * Created by Pearl on 2018-07-19.
 */

import android.app.Application;
import android.content.Context;

import com.secneo.sdk.Helper;

public class MApplication extends Application {
    private DemoApplication demoApplication;

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        Helper.install(MApplication.this);
        if (demoApplication == null) {
            demoApplication = new DemoApplication();
            demoApplication.setContext(this);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        demoApplication.onCreate();
    }
}