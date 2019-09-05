package com.bangmart.bmtvendingmachinedemo.view;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bangmart.bmtvendingmachinedemo.R;


/**
 * Created by Administrator on 2017/3/28.
 */

public class CustomToast {

    public static int NO_Pic=-1;

    private Toast mToast;
    private static Toast toast;
    private CustomToast(Context context, CharSequence text,int rId, int duration) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
        Typeface face = Typeface.createFromAsset (context.getAssets() , "fonts/huakangheijing.ttf" );
        TextView textView = (TextView) v.findViewById(R.id.totas_tv);
        textView.setTypeface(face);
        textView.setText(text);
        ImageView imageView=(ImageView) v.findViewById(R.id.totas_iv);
        imageView.setBackgroundResource(rId);
        mToast = new Toast(context);
        mToast.setDuration(duration);
        mToast.setView(v);
    }

    public static CustomToast makeText(Context context, CharSequence text,int rId, int duration) {
        return new CustomToast(context, text,rId, duration);
    }
    public void show() {
        if (mToast != null) {
            mToast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.CENTER, 0, 0);
            //mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.show();
        }
    }

    public static void showToast(Context context, CharSequence text,int rId, int duration) {
        if (toast == null) {
            View v = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
            Typeface face = Typeface.createFromAsset (context.getAssets() , "fonts/huakangheijing.ttf" );
            TextView textView = (TextView) v.findViewById(R.id.totas_tv);
            textView.setTypeface(face);
            textView.setText(text);
            if(rId!=-1){
                ImageView imageView=(ImageView) v.findViewById(R.id.totas_iv);
                imageView.setBackgroundResource(rId);
            }
            toast = new Toast(context);
            toast.setGravity(Gravity.FILL, 0, 0);
            toast.setDuration(duration);
            toast.setView(v);
        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
            Typeface face = Typeface.createFromAsset (context.getAssets() , "fonts/huakangheijing.ttf" );
            TextView textView = (TextView) v.findViewById(R.id.totas_tv);
            textView.setTypeface(face);
            textView.setText(text);
            if(rId!=-1){
                ImageView imageView=(ImageView) v.findViewById(R.id.totas_iv);
                imageView.setBackgroundResource(rId);
            }
            toast.setGravity(Gravity.FILL, 0, 0);
            toast.setDuration(duration);
            toast.setView(v);
        }
        toast.show();
    }

}
