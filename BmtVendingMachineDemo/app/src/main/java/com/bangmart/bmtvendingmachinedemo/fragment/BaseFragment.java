package com.bangmart.bmtvendingmachinedemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.bangmart.bmtvendingmachinedemo.dialog.BaseProgressDialog;
import com.bangmart.bmtvendingmachinedemo.util.ViewUtil;

/**
 * Created by zhoujq.
 */

public abstract class BaseFragment extends Fragment {
	private Context mContext;
	private boolean mHideImeTouchOutsideEditText=true;
	private BaseProgressDialog mBaseProgressDialog;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
	
		View view =initView(inflater,container);
		if(isHideImeTouchOutsideEditText()){
            ViewUtil.setHideIme(getActivity(), view);
        }
		
		initListener();
		
		return view;
	}

	/**
	 * init data in onCreate()
	 * 
	 * initData()->initView()->initListener()
	 */
	protected abstract void initData();
	
	/**
	 * init view in onCreate()
	 * 
	 * initData()->initView()->initListener()
	 * @return 
	 */
	protected abstract View initView(LayoutInflater inflater, ViewGroup container);
		
	
	/**
	 * init Listener in onCreate()
	 * 
	 * initData()->initView()->initListener()
	 */
	protected abstract void initListener();
	
	public Context getContext() {
		return mContext;
	}

	public void setContext(Context context) {
		mContext = context;
	}
	
    public boolean isHideImeTouchOutsideEditText() {
        return mHideImeTouchOutsideEditText;
    }

    public void setHideImeTouchOutsideEditText(boolean hideImeTouchOutsideEditText) {
        mHideImeTouchOutsideEditText = hideImeTouchOutsideEditText;
    }

	public BaseProgressDialog getBaseProgressDialog() {
		if (null == mBaseProgressDialog) {
			if(null==getContext()){
				if(null!=getActivity()){
					setContext(getActivity());
				}else {
					return null;
				}
			}
			mBaseProgressDialog = new BaseProgressDialog(getContext());
			mBaseProgressDialog.setCanceledOnTouchOutside(true);
			Window window = mBaseProgressDialog.getWindow();
			if (mBaseProgressDialog != null && window != null) {
				WindowManager.LayoutParams attr = window.getAttributes();
				if (attr != null) {
					attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
					attr.width = ViewGroup.LayoutParams.WRAP_CONTENT;
					attr.gravity = Gravity.CENTER;//设置dialog 在布局中的位置
				}
			}
		}
		return mBaseProgressDialog;
	}

	public void setBaseProgressDialog(BaseProgressDialog baseProgressDialog) {
		mBaseProgressDialog = baseProgressDialog;
	}

}
