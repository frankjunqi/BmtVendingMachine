package com.seekwork.bangmart;

import android.app.Application;

import com.bangmart.nt.sys.Logger;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by gongtao on 2017/12/29.
 */

public class NTApplication extends Application {
    private static NTApplication mSeekersoftApp;

    @Override
    public void onCreate() {
        super.onCreate();

        // 异常处理，不需要处理时注释掉这两句即可！
        CrashHandler crashHandler = CrashHandler.getInstance();
        // 注册crashHandler
        crashHandler.init(getApplicationContext());

        // 初始化bugly
        CrashReport.initCrashReport(getApplicationContext(), "ec127254a0", true);
    }

    public static Application getInstance() {
        return mSeekersoftApp;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Logger.flush();
    }
}
