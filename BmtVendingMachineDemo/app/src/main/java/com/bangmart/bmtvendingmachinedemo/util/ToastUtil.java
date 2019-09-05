package com.bangmart.bmtvendingmachinedemo.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;


/**
 * Created by zhoujq.
 */

public class ToastUtil {
	public static void toast(Context context, int resId, int duration){
		do {
			if(null==context){
				break;
			}

			Toast.makeText(context, resId, duration).show();
		}while (false);

		return;
	}
	
	public static void toast(Context context, String text, int duration){
		do{
			if(null==context){
				break;
			}

			Toast.makeText(context, text, duration).show();
		}while (false);

		return;
	}
	
	public static void toastLengthshort(Context context, String text){
		do{
			if(null==context){
				break;
			}

			ToastUtil.toast(context, text, Toast.LENGTH_SHORT);
		}while (false);

		return;
	}
	
	public static void toastLengthLong(Context context, String text){
		do{
			if(null==context){
				break;
			}

			ToastUtil.toast(context, text, Toast.LENGTH_LONG);
		}while (false);

		return;
	}
	
	public static void toastLengthshort(Context context, int resId){
		do{
			if(null==context){
				break;
			}

			ToastUtil.toast(context, resId, Toast.LENGTH_SHORT);
		}while (false);

		return;
	}
	
	public static void toastLengthLong(Context context, int resId){
		do{
			if(null==context){
				break;
			}

			ToastUtil.toast(context, resId, Toast.LENGTH_LONG);
		}while (false);

		return;
	}
	public static void toastShowCentrLong(Context context, int resId){
		do{
			if(null==context){
				break;
			}

			ToastUtil.toast(context, resId, Toast.LENGTH_LONG);
		}while (false);

		return;
	}
	public static void toastShowCentrShort(Context context, int resId){
		do{
			if(null==context){
				break;
			}
			Toast toast = Toast.makeText(context, resId, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}while (false);

		return;
	}
	public static void toastShowCentrLong(Context context, String text){
		do{
			if(null==context){
				break;
			}
			Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}while (false);

		return;
	}
	public static void toastShowCentrShort(Context context, String text){
		do{
			if(null==context){
				break;
			}
			Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}while (false);

		return;
	}


/*	*//**
	 * 在主线程显示Toast
	 * @param tips
     *//*
	public static void  toastLengthLongInMainThread(String tips){
		do{
			if(true == CommonUtil.isEmpty(tips)){
				break;
			}

			Observable.just(tips).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Object>() {
				@Override
				public void call(Object o) {
					ToastUtil.toastLengthLong(AppApplication.mContext,o.toString());
				}
			});
		}while (false);

		return;
	}

	public static void  toastLengthLongInMainThread2(String tips){
		do{
			if(true ==CommonUtil.isEmpty(tips)){
				break;
			}

			Observable.just(tips).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Object>() {
				@Override
				public void call(Object o) {
					//ToastUtil.toastLengthLong(AppApplication.mContext,o.toString());
					CustomToast.showToast(AppApplication.mContext,o.toString(), R.mipmap.smail, Toast.LENGTH_LONG);
				}
			});
		}while (false);

		return;
	}*/
}
