package com.bangmart.bmtvendingmachinedemo.helper;

import com.bangmart.bmtvendingmachinedemo.NTApplication;
import com.bangmart.bmtvendingmachinedemo.util.SharedPreferencesUtil;

/**
 * Created by zhoujq on 2019/8/9.
 */
public class SettingsHelper {
    private static final String TAG = "SettingsHelper";
    private static SettingsHelper mInstance;

    public static final String KEY_DEVICE_NO="KEY_DEVICE_NO";

    private String mDeviceNo;

    public static SettingsHelper getInstance() {
        if (null == mInstance) {
            mInstance = new SettingsHelper();
        }

        return mInstance;
    }

    private SettingsHelper() {
        initSettingsHelper();
    }

    private void initSettingsHelper() {
        mDeviceNo= SharedPreferencesUtil.getString(NTApplication.getContext(), KEY_DEVICE_NO,BmtVendingMachineHelper.DEFAULT_DEVICE_NO);

        return;
    }

    public String getDeviceNo() {
        return mDeviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        SharedPreferencesUtil.putString(NTApplication.getContext(),KEY_DEVICE_NO,deviceNo);
        mDeviceNo = deviceNo;
    }
}
