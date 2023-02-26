package com.bely.easysync;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SyncApplication extends Application {
    static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Network.getInstance().start(Utils.getSavedIP());
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Network.getInstance().shutdown();
    }
}
