package com.jonathanfinerty.liquid;

import android.app.Application;

import timber.log.Timber;

public class LiquidApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
