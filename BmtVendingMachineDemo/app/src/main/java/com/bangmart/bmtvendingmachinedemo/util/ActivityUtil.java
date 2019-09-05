
package com.bangmart.bmtvendingmachinedemo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by zhoujq.
 */

public class ActivityUtil {
    public static void launch(Context context, Class<?> cls) {
        do {
            if ((null == context) || (null == cls)) {
                break;
            }

            Intent intent = new Intent(context, cls);
            context.startActivity(intent);
        } while (false);

        return;
    }

    public static void launch(Context context, Class<?> cls, String key, Serializable value) {
        do {
            if ((null == context) || (null == cls) || (TextUtils.isEmpty(key)) || (null == value)) {
                break;
            }

            Intent intent = new Intent(context, cls);
            Bundle bundle = new Bundle();
            bundle.putSerializable(key, value);

            intent.putExtras(bundle);

            context.startActivity(intent);
        } while (false);

        return;
    }

    public static void back(Activity activity){
        if(null!=activity){
            activity.finish();
        }
    }
}
