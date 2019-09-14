package com.seekwork.bangmart;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.bangmart.nt.sys.ContextInfo;
import com.bangmart.nt.sys.LogToFile;
import com.bangmart.nt.sys.Logger;
import com.seekwork.bangmart.util.Variable;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by gongtao on 2017/12/29.
 */

public class NTApplication extends Application {
    private static NTApplication mSeekersoftApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mSeekersoftApp = this;

        // 异常处理，不需要处理时注释掉这两句即可！
        CrashHandler crashHandler = CrashHandler.getInstance();
        // 注册crashHandler
        crashHandler.init(getApplicationContext());
        // 初始化bugly
        CrashReport.initCrashReport(getApplicationContext(), "ec127254a0", true);
        CrashReport.setUserId(getApplicationContext(), Build.FINGERPRINT);

        initScreenProperties(this);

        ContextInfo.init(this);
        LogToFile.initialize(this);
    }

    public static Application getInstance() {
        return mSeekersoftApp;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Logger.sLogger4Machine.flush();
    }

    public static void initScreenProperties(Context c) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);

        Variable.DESITY = dm.density;
        Variable.WIDTH = dm.widthPixels;
        Variable.HEIGHT = dm.heightPixels;
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                Variable.HEIGHT = (Integer) Display.class.getMethod("getRawHeight").invoke(dm);
            } catch (Exception ignored) {
            }
        else if (Build.VERSION.SDK_INT >= 17) {
            try {
                android.graphics.Point realSize = new android.graphics.Point();
                Display.class.getMethod("getRealSize", android.graphics.Point.class).invoke(dm, realSize);
                Variable.HEIGHT = realSize.y;
            } catch (Exception ignored) {
            }
        }
    }
}
