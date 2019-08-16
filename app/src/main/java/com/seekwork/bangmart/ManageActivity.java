package com.seekwork.bangmart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.seekwork.bangmart.util.SeekerSoftConstant;

/**
 * 管理页面
 */
public class ManageActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_exit_sys, btn_exit_manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        btn_exit_sys = findViewById(R.id.btn_exit_sys);
        btn_exit_manager = findViewById(R.id.btn_exit_manager);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_exit_sys:
                Intent intentExit = new Intent(ManageActivity.this, MainActivity.class);
                intentExit.putExtra(SeekerSoftConstant.EXITAPP, 1);
                intentExit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentExit);
                ManageActivity.this.finish();
                break;
            case R.id.btn_exit_manager:
                ManageActivity.this.finish();
                break;
        }
    }
}