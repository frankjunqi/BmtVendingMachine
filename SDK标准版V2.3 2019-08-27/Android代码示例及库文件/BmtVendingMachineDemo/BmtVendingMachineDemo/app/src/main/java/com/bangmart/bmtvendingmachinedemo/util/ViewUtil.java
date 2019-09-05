package com.bangmart.bmtvendingmachinedemo.util;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by zhoujq.
 */

public class ViewUtil {

	public static void showView(View view) {
		if (null == view) {
			return;
		}

		if (View.VISIBLE != view.getVisibility()) {
			view.setVisibility(View.VISIBLE);
		}
	}
	
	public static void invisibleView(View view) {
		if (null == view) {
			return;
		}
		if (View.INVISIBLE != view.getVisibility()) {
			view.setVisibility(View.INVISIBLE);
		}

		return;
	}
	
	public static void hideView(View view) {
		if (null == view) {
			return;
		}
		if (View.GONE != view.getVisibility()) {
			view.setVisibility(View.GONE);
		}

		return;
	}
	
	 /**
     * hide soft keyboard on android after clicking outside EditText
     * 
     * @param view
     */
    public static void setHideIme(final Activity activity, View view) {
        if(null==activity||null==view){
            return;
        }

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ViewUtil.hideSoftKeyboard(activity);
                    return false;
                }

            });
        }

        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setHideIme(activity,innerView);
            }
        }
    }
    
    /**
     * hide soft keyboard
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        if(null==activity){
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(null!=inputMethodManager){
            View view=activity.getCurrentFocus();
            if(null!=view){
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
          
        }
    }

}
