package com.seekwork.bangmart.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.seekwork.bangmart.InitActivity;


/**
 *
 */

public class AutoStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 开机启动
        Intent i = new Intent(context, InitActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}