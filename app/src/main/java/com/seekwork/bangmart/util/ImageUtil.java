package com.seekwork.bangmart.util;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

/**
 * Created by lingcheng on 15/10/26.
 */
public class ImageUtil {
    private static ImageUtil ourInstance = new ImageUtil();

    public static ImageUtil getInstance() {
        return ourInstance;
    }

    public void displayImage(Context context, ImageView image, int resId) {
        if (context instanceof FragmentActivity) {
            Activity a = (Activity) context;
            if (a.isFinishing())
                return;
        }
        Glide.with(context)
                .load(resId)
                .dontAnimate()
                .into(image);
    }

    public void displayImageFile(Context context, ImageView image, File file) {
        if (context instanceof FragmentActivity) {
            Activity a = (Activity) context;
            if (a.isFinishing())
                return;
        }
        Glide.with(context)
                .load(file)
                .dontAnimate()
                .into(image);
    }

    public void displayImage(Context context, ImageView image, File url) {
        if (url != null) {
            Glide.with(context)
                    .load(url)
                    .dontAnimate()
                    .fitCenter()
                    .into(image);
        }
    }

}
