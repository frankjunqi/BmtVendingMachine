package com.bangmart.bmtvendingmachinedemo;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.bangmart.bmtvendingmachinedemo.constant.BuglyConstant;
import com.bangmart.bmtvendingmachinedemo.test.CrashHandler;
import com.bangmart.nt.sys.ContextInfo;
import com.bangmart.nt.sys.LogToFile;
import com.bangmart.nt.sys.Logger;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by gongtao on 2017/12/29.
 */

public class NTApplication extends Application {

    public static Context sContext;
    @Override
    public void onCreate() {
        super.onCreate();
        sContext=this;

        initBugly();
        /**
         * start:init log
         */
        //放到Application
        ContextInfo.init(sContext);
        LogToFile.initialize(sContext);
        /**
         * end:init log
         */



        // 异常处理，不需要处理时注释掉这两句即可！ 集成bugly 注释掉此两行
     /*   CrashHandler crashHandler = CrashHandler.getInstance();
        // 注册crashHandler
        crashHandler.init(getApplicationContext());*/

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Logger.sLogger4Machine.flush();
    }

    public static Context getContext() {
        return sContext;
    }

    public static void setContext(Context context) {
        sContext = context;
    }

    /**
     * 初始化bugly
     */
    private void initBugly(){
        CrashReport.initCrashReport(getApplicationContext(), BuglyConstant.APPID, false);

        CrashReport.setUserId(getApplicationContext(), Build.FINGERPRINT);

    }
}
