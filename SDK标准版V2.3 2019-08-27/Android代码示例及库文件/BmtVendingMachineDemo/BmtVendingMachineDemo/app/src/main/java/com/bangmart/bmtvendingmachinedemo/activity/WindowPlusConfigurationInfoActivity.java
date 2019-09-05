package com.bangmart.bmtvendingmachinedemo.activity;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.bangmart.bmtvendingmachinedemo.fragment.WindowPlusConfigurationInfoFragment;
import com.bangmart.bmtvendingmachinedemo.util.ActivityUtil;

/**
 * Created by zhoujq on 2018/9/10.
 * W+配置信息
 */
public class WindowPlusConfigurationInfoActivity extends BaseSingleFragmentActivity {
    private static final String TAG = "WindowPlusConfigurationInfoActivity";
    @Override
    protected Fragment createFragment() {
        return new WindowPlusConfigurationInfoFragment();
    }

    @Override
    protected boolean isRealTimeLoadFragment() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static void launch(Context context) {
        ActivityUtil.launch(context, WindowPlusConfigurationInfoActivity.class);
        return;
    }
}
