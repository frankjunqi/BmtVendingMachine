package com.bangmart.bmtvendingmachinedemo.fragment;

import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bangmart.bmtvendingmachinedemo.R;
import com.bangmart.bmtvendingmachinedemo.androidboard.util.AndroidBoardModelUtil;
import com.bangmart.bmtvendingmachinedemo.camera.CameraUtil;
import com.bangmart.bmtvendingmachinedemo.constant.Constant;
import com.bangmart.bmtvendingmachinedemo.constant.RelayConfig;
import com.bangmart.bmtvendingmachinedemo.util.ActivityUtil;
import com.bangmart.bmtvendingmachinedemo.util.BmtVendingMachineUtil;
import com.bangmart.bmtvendingmachinedemo.util.CommonUtil;
import com.bangmart.bmtvendingmachinedemo.util.ToastUtil;
import com.bangmart.bmtvendingmachinedemo.util.ViewUtil;
import com.bangmart.bmtvendingmachinedemo.view.CustomToast;
import com.bangmart.nt.command.CommandDef;
import com.bangmart.nt.common.util.DataUtil;
import com.bangmart.nt.vendingmachine.constant.BmtMsgConstant;
import com.bangmart.nt.vendingmachine.ipsmsg.constant.IpsConstant;
import com.bangmart.nt.vendingmachine.ipsmsg.constant.IpsMcsConstant;
import com.bangmart.nt.vendingmachine.ipsmsg.constant.IpsPcsConstant;
import com.bangmart.nt.vendingmachine.ipsmsg.model.ReqRwSystemParameter;
import com.bangmart.nt.vendingmachine.ipsmsg.model.RspRwSystemParameter;
import com.bangmart.nt.vendingmachine.model.ReqRelayControl;
import com.bangmart.nt.vendingmachine.model.ReqRwPickOffset;
import com.bangmart.nt.vendingmachine.model.ReqRwPopVal;
import com.bangmart.nt.vendingmachine.model.ReqTempHumidityControl;
import com.bangmart.nt.vendingmachine.model.RspGetSensorState;
import com.bangmart.nt.vendingmachine.model.RspRelayControl;
import com.bangmart.nt.vendingmachine.model.RspRwPickOffset;
import com.bangmart.nt.vendingmachine.model.RspRwPopVal;
import com.bangmart.nt.vendingmachine.model.RspTempHumidityControl;
import com.tencent.bugly.crashreport.CrashReport;


import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zhoujq on 2018/9/10.
 * W+配置信息
 */
public class WindowPlusConfigurationInfoFragment extends BaseFragment {
    private ImageView mBackIv;

    /**
     * 随机漫步
     */
/*    private Button mStartRandomWalkBtn;
    private Button mEndRandomWalkBtn;*/

    private Button mAliCameraTestBtn;
    private EditText mOutGoodsPortHeightEt;
    private Button mReadOutGoodsPortHeightBtn;
    private Button mWriteOutGoodsPortHeightBtn;

    private EditText mPickupHeightCompensationValueEt;
    private Button mReadPickupHeightCompensationValueBtn;
    private Button mWritePickupHeightCompensationValueBtn;

    /**
     * start: X轴电机启动速度(rpm)
     */
    private EditText mXAxisMotorStartingSpeedEt;
    private Button mReadXAxisMotorStartingSpeedBtn;
    private Button mWriteXAxisMotorStartingSpeedBtn;
    /**
     * end: X轴电机启动速度(rpm)
     */
    /**
     * start: X轴电机最高速度(rpm)
     */
    private EditText mXAxisMotorMaximumSpeedEt;
    private Button mReadXAxisMotorMaximumSpeedBtn;
    private Button mWriteXAxisMotorMaximumSpeedBtn;
    /**
     * start: X轴电机最高速度(rpm)
     */

    /**
     * start: X轴电机加速距离(steps)
     */
    private EditText mXAxisMotorAccelerationDistanceEt;
    private Button mReadXAxisMotorAccelerationDistanceBtn;
    private Button mWriteXAxisMotorAccelerationDistanceBtn;
    /**
     * end: X轴电机加速距离(steps)
     */

    /**
     * start: Y轴电机启动速度(rpm)
     */
    private EditText mYAxisMotorStartingSpeedEt;
    private Button mReadYAxisMotorStartingSpeedBtn;
    private Button mWriteYAxisMotorStartingSpeedBtn;
    /**
     * end: Y轴电机启动速度(rpm)
     */
    /**
     * start: Y轴电机最高速度(rpm)
     */
    private EditText mYAxisMotorMaximumSpeedEt;
    private Button mReadYAxisMotorMaximumSpeedBtn;
    private Button mWriteYAxisMotorMaximumSpeedBtn;
    /**
     * start: Y轴电机最高速度(rpm)
     */

    /**
     * start: Y轴电机加速距离(steps)
     */
    private EditText mYAxisMotorAccelerationDistanceEt;
    private Button mReadYAxisMotorAccelerationDistanceBtn;
    private Button mWriteYAxisMotorAccelerationDistanceBtn;
    /**
     * end: Y轴电机加速距离(steps)
     */

    private EditText mCarWheelCircumferenceEt;
    private Button mReadCarWheelCircumferenceBtn;
    private Button mWriteCarWheelCircumferenceBtn;

    private EditText mLiftingSynchronousWheelCircumferenceEt;
    private Button mReadLiftingSynchronousWheelCircumferenceBtn;
    private Button mWriteLiftingSynchronousWheelCircumferenceBtn;

    private EditText mSliderSynchronizationWheelCircumferenceEt;
    private Button mReadSliderSynchronizationWheelCircumferenceBtn;
    private Button mWriteSliderSynchronizationWheelCircumferenceBtn;


    private EditText mSettingTempEt;
    private EditText mTempAreaIndexEt;
    private TextView mActualTempTv;
    private Button mReadTempBtn;
    private Button mWriteTempBtn;

    private Button mGetSensorStateBtn;
    private TextView mSensorStateTv;

    /**
     * start:继电器
     */
    private EditText mAreaIndexEt;
    private EditText mRelayIndexEt;
    private EditText mActionEt;
    private EditText mResetTimeEt;
    private Button mOperatingRelayBtn;

    private EditText mCommonAreaIndexEt;
    private EditText mCommonResetTimeEt;

    /**
     * start:旧版继电器默认配置
     */
    private Button mOpenElectronicLocksBtn;
    private Button mCloseElectronicLocksBtn;

    private Button mOpenGatingBtn;
    private Button mCloseGatingBtn;

    private Button mRestartCarBtn;
    private Button mOpenCarBtn;
    private Button mCloseCarBtn;

    private Button mRestartOneMachineBtn;
    private Button mOpenOneMachineBtn;
    private Button mCloseOneMachineBtn;

    private Button mOpenLedBtn;
    private Button mCloseLedBtn;

    private Button mOpenFanBtn;
    private Button mCloseFanBtn;

    private Button mOpenCompressorBtn;
    private Button mCloseCompressorBtn;
    /**
     * end:旧版继电器默认配置
     */
    /**
     * start:新版继电器默认配置
     */
    private Button mNewOpenElectronicLocksBtn;
    private Button mNewCloseElectronicLocksBtn;

    private Button mNewOpenGatingBtn;
    private Button mNewCloseGatingBtn;

    private Button mNewRestartCarBtn;
    private Button mNewOpenCarBtn;
    private Button mNewCloseCarBtn;

    private Button mNewRestartOneMachineBtn;
    private Button mNewOpenOneMachineBtn;
    private Button mNewCloseOneMachineBtn;

    private Button mNewOpenLedBtn;
    private Button mNewCloseLedBtn;

    private Button mNewOpenFanBtn;
    private Button mNewCloseFanBtn;

    private Button mNewOpenCompressorBtn;
    private Button mNewCloseCompressorBtn;

    private Button mNewOpenHeatedGlassBtn;
    private Button mNewCloseHeatedGlassBtn;
    /**
     * end:新版继电器默认配置
     */
    /**
     * end:继电器
     */

    @Override
    protected void initData() {
        setContext(getActivity());
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_window_plus_configuration_info, container, false);
        mBackIv = (ImageView) view.findViewById(R.id.back_image_view);



        mAliCameraTestBtn = (Button) view.findViewById(R.id.btn_ali_camera_test);

        mOutGoodsPortHeightEt = (EditText) view.findViewById(R.id.out_goods_port_height_edit_text);
        mReadOutGoodsPortHeightBtn = (Button) view.findViewById(R.id.read_out_goods_port_height_btn);
        mWriteOutGoodsPortHeightBtn = (Button) view.findViewById(R.id.write_out_goods_port_height_btn);

        mPickupHeightCompensationValueEt = (EditText) view.findViewById(R.id.pickup_height_compensation_value_edit_text);
        mReadPickupHeightCompensationValueBtn = (Button) view.findViewById(R.id.read_pickup_height_compensation_value_btn);
        mWritePickupHeightCompensationValueBtn = (Button) view.findViewById(R.id.write_pickup_height_compensation_value_btn);

        mXAxisMotorStartingSpeedEt = (EditText) view.findViewById(R.id.x_axis_motor_starting_speed_edit_text);
        mReadXAxisMotorStartingSpeedBtn = (Button) view.findViewById(R.id.read_x_axis_motor_starting_speed_btn);
        mWriteXAxisMotorStartingSpeedBtn = (Button) view.findViewById(R.id.write_x_axis_motor_starting_speed_btn);

        mXAxisMotorMaximumSpeedEt = (EditText) view.findViewById(R.id.x_axis_motor_maximum_speed_edit_text);
        mReadXAxisMotorMaximumSpeedBtn = (Button) view.findViewById(R.id.read_x_axis_motor_maximum_speed_btn);
        mWriteXAxisMotorMaximumSpeedBtn = (Button) view.findViewById(R.id.write_x_axis_motor_maximum_speed_btn);


        mXAxisMotorAccelerationDistanceEt = (EditText) view.findViewById(R.id.x_axis_motor_acceleration_distance_edit_text);
        mReadXAxisMotorAccelerationDistanceBtn = (Button) view.findViewById(R.id.read_x_axis_motor_acceleration_distance_btn);
        mWriteXAxisMotorAccelerationDistanceBtn = (Button) view.findViewById(R.id.write_x_axis_motor_acceleration_distance_btn);


        mYAxisMotorStartingSpeedEt = (EditText) view.findViewById(R.id.y_axis_motor_starting_speed_edit_text);
        mReadYAxisMotorStartingSpeedBtn = (Button) view.findViewById(R.id.read_y_axis_motor_starting_speed_btn);
        mWriteYAxisMotorStartingSpeedBtn = (Button) view.findViewById(R.id.write_y_axis_motor_starting_speed_btn);

        mYAxisMotorMaximumSpeedEt = (EditText) view.findViewById(R.id.y_axis_motor_maximum_speed_edit_text);
        mReadYAxisMotorMaximumSpeedBtn = (Button) view.findViewById(R.id.read_y_axis_motor_maximum_speed_btn);
        mWriteYAxisMotorMaximumSpeedBtn = (Button) view.findViewById(R.id.write_y_axis_motor_maximum_speed_btn);


        mYAxisMotorAccelerationDistanceEt = (EditText) view.findViewById(R.id.y_axis_motor_acceleration_distance_edit_text);
        mReadYAxisMotorAccelerationDistanceBtn = (Button) view.findViewById(R.id.read_y_axis_motor_acceleration_distance_btn);
        mWriteYAxisMotorAccelerationDistanceBtn = (Button) view.findViewById(R.id.write_y_axis_motor_acceleration_distance_btn);


        mCarWheelCircumferenceEt = (EditText) view.findViewById(R.id.car_wheel_circumference_edit_text);
        mReadCarWheelCircumferenceBtn = (Button) view.findViewById(R.id.read_car_wheel_circumference_btn);
        mWriteCarWheelCircumferenceBtn = (Button) view.findViewById(R.id.write_car_wheel_circumference_btn);

        mLiftingSynchronousWheelCircumferenceEt = (EditText) view.findViewById(R.id.lifting_synchronous_wheel_circumference_edit_text);
        mReadLiftingSynchronousWheelCircumferenceBtn = (Button) view.findViewById(R.id.read_lifting_synchronous_wheel_circumference_btn);
        mWriteLiftingSynchronousWheelCircumferenceBtn = (Button) view.findViewById(R.id.write_lifting_synchronous_wheel_circumference_btn);

        mSliderSynchronizationWheelCircumferenceEt = (EditText) view.findViewById(R.id.slider_synchronization_wheel_circumference_edit_text);
        mReadSliderSynchronizationWheelCircumferenceBtn = (Button) view.findViewById(R.id.read_slider_synchronization_wheel_circumference_btn);
        mWriteSliderSynchronizationWheelCircumferenceBtn = (Button) view.findViewById(R.id.write_slider_synchronization_wheel_circumference_btn);


        mTempAreaIndexEt = (EditText) view.findViewById(R.id.temp_area_index_edit_text);
        mSettingTempEt = (EditText) view.findViewById(R.id.setting_temp_edit_text);
        mActualTempTv = (TextView) view.findViewById(R.id.actual_temp_text_view);
        mReadTempBtn = (Button) view.findViewById(R.id.read_temp_btn);
        mWriteTempBtn = (Button) view.findViewById(R.id.write_temp_btn);

        mGetSensorStateBtn = (Button) view.findViewById(R.id.get_sensor_state_btn);
        mSensorStateTv = (TextView) view.findViewById(R.id.sensor_state_text_view);

        mAreaIndexEt = (EditText) view.findViewById(R.id.area_index_edit_text);
        mRelayIndexEt = (EditText) view.findViewById(R.id.relay_index_edit_text);
        mActionEt = (EditText) view.findViewById(R.id.action_edit_text);
        mResetTimeEt = (EditText) view.findViewById(R.id.reset_time_edit_text);
        mOperatingRelayBtn = (Button) view.findViewById(R.id.operating_relay_btn);


        /**
         * start:继电器
         */
        mCommonAreaIndexEt = (EditText) view.findViewById(R.id.common_area_index_edit_text);
        mCommonResetTimeEt = (EditText) view.findViewById(R.id.common_reset_time_edit_text);

        mOpenElectronicLocksBtn = (Button) view.findViewById(R.id.open_electronic_locks_btn);
        mCloseElectronicLocksBtn = (Button) view.findViewById(R.id.close_electronic_locks_btn);
        /**
         *关隐藏掉
         */
        ViewUtil.hideView(mCloseElectronicLocksBtn);

        mOpenGatingBtn = (Button) view.findViewById(R.id.open_gating_btn);
        mCloseGatingBtn = (Button) view.findViewById(R.id.close_gating_btn);

        mRestartCarBtn = (Button) view.findViewById(R.id.restart_car_btn);
        mOpenCarBtn = (Button) view.findViewById(R.id.open_car_btn);
        mCloseCarBtn = (Button) view.findViewById(R.id.close_car_btn);
        ViewUtil.hideView(mOpenCarBtn);
        ViewUtil.hideView(mCloseCarBtn);

        mRestartOneMachineBtn = (Button) view.findViewById(R.id.restart_one_machine_btn);
        mOpenOneMachineBtn = (Button) view.findViewById(R.id.open_one_machine_btn);
        mCloseOneMachineBtn = (Button) view.findViewById(R.id.close_one_machine_btn);
        ViewUtil.hideView(mOpenOneMachineBtn);
        ViewUtil.hideView(mCloseOneMachineBtn);

        mOpenLedBtn = (Button) view.findViewById(R.id.open_led_btn);
        mCloseLedBtn = (Button) view.findViewById(R.id.close_led_btn);

        mOpenFanBtn = (Button) view.findViewById(R.id.open_fan_btn);
        mCloseFanBtn = (Button) view.findViewById(R.id.close_fan_btn);

        mOpenCompressorBtn = (Button) view.findViewById(R.id.open_compressor_btn);
        mCloseCompressorBtn = (Button) view.findViewById(R.id.close_compressor_btn);
        /**
         * end:继电器
         */

        /**
         * start:新版继电器
         */

        mNewOpenElectronicLocksBtn = (Button) view.findViewById(R.id.new_open_electronic_locks_btn);
        mNewCloseElectronicLocksBtn = (Button) view.findViewById(R.id.new_close_electronic_locks_btn);
        /**
         *关隐藏掉
         */
        ViewUtil.hideView(mNewCloseElectronicLocksBtn);

        mNewOpenGatingBtn = (Button) view.findViewById(R.id.new_open_gating_btn);
        mNewCloseGatingBtn = (Button) view.findViewById(R.id.new_close_gating_btn);

        mNewRestartCarBtn = (Button) view.findViewById(R.id.new_restart_car_btn);
        mNewOpenCarBtn = (Button) view.findViewById(R.id.new_open_car_btn);
        mNewCloseCarBtn = (Button) view.findViewById(R.id.new_close_car_btn);
        ViewUtil.hideView(mNewOpenCarBtn);
        ViewUtil.hideView(mNewCloseCarBtn);

        mNewRestartOneMachineBtn = (Button) view.findViewById(R.id.new_restart_one_machine_btn);
        mNewOpenOneMachineBtn = (Button) view.findViewById(R.id.new_open_one_machine_btn);
        mNewCloseOneMachineBtn = (Button) view.findViewById(R.id.new_close_one_machine_btn);
        ViewUtil.hideView(mNewOpenOneMachineBtn);
        ViewUtil.hideView(mNewCloseOneMachineBtn);

        mNewOpenLedBtn = (Button) view.findViewById(R.id.new_open_led_btn);
        mNewCloseLedBtn = (Button) view.findViewById(R.id.new_close_led_btn);

        mNewOpenFanBtn = (Button) view.findViewById(R.id.new_open_fan_btn);
        mNewCloseFanBtn = (Button) view.findViewById(R.id.new_close_fan_btn);

        mNewOpenCompressorBtn = (Button) view.findViewById(R.id.new_open_compressor_btn);
        mNewCloseCompressorBtn = (Button) view.findViewById(R.id.new_close_compressor_btn);

        mNewOpenHeatedGlassBtn = (Button) view.findViewById(R.id.new_open_heated_glass_btn);
        mNewCloseHeatedGlassBtn = (Button) view.findViewById(R.id.new_close_heated_glass_btn);

        /**
         * end:新版继电器
         */
        return view;
    }

    @Override
    protected void initListener() {
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CrashReport.testJavaCrash();
                ActivityUtil.back(getActivity());


            }
        });

        /**
         * 支付宝刷脸付摄像头测试
         */
        mAliCameraTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aliCameraTest();
            }
        });

        mReadOutGoodsPortHeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readOutGoodsPortHeight();
            }
        });

        mWriteOutGoodsPortHeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeOutGoodsPortHeight();
            }
        });

        mReadPickupHeightCompensationValueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readPickupHeightCompensationValue();
            }
        });

        mWritePickupHeightCompensationValueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writePickupHeightCompensationValue();
            }
        });

        mReadXAxisMotorStartingSpeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readXAxisMotorStartingSpeed();
            }
        });

        mWriteXAxisMotorStartingSpeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeXAxisMotorStartingSpeed();
            }
        });

        mReadXAxisMotorMaximumSpeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readXAxisMotorMaximumSpeed();
            }
        });

        mWriteXAxisMotorMaximumSpeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeXAxisMotorMaximumSpeed();
            }
        });

        mReadXAxisMotorAccelerationDistanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readXAxisMotorAccelerationDistance();
            }
        });

        mWriteXAxisMotorAccelerationDistanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeXAxisMotorAccelerationDistance();
            }
        });

        mReadYAxisMotorStartingSpeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readYAxisMotorStartingSpeed();
            }
        });

        mWriteYAxisMotorStartingSpeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeYAxisMotorStartingSpeed();
            }
        });

        mReadYAxisMotorMaximumSpeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readYAxisMotorMaximumSpeed();
            }
        });

        mWriteYAxisMotorMaximumSpeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeYAxisMotorMaximumSpeed();
            }
        });

        mReadYAxisMotorAccelerationDistanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readYAxisMotorAccelerationDistance();
            }
        });

        mWriteYAxisMotorAccelerationDistanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeYAxisMotorAccelerationDistance();
            }
        });


        mReadCarWheelCircumferenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readCarWheelCircumference();
            }
        });

        mWriteCarWheelCircumferenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeCarWheelCircumference();
            }
        });

        mReadLiftingSynchronousWheelCircumferenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readLiftingSynchronousWheelCircumference();
            }
        });

        mWriteLiftingSynchronousWheelCircumferenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeLiftingSynchronousWheelCircumference();
            }
        });

        mReadSliderSynchronizationWheelCircumferenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readSliderSynchronizationWheelCircumference();
            }
        });

        mWriteSliderSynchronizationWheelCircumferenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeSliderSynchronizationWheelCircumference();
            }
        });

        mReadTempBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readTemp();
            }
        });

        mWriteTempBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeTemp();
            }
        });


        mGetSensorStateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSensorState();
            }
        });

        mOperatingRelayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay();
            }
        });
        /**
         * start:旧版继电器默认配置
         */
        mOpenElectronicLocksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* int openElectronicLocksResetTimeSec=40;
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.ELECTRONIC_LOCKS,BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON,openElectronicLocksResetTimeSec);*/

                /**
                 * 应设备端要求 开-off-1秒
                 */
                int openElectronicLocksResetTimeSec = 1;
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.ELECTRONIC_LOCKS, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF, openElectronicLocksResetTimeSec, RelayConfig.OLD_CONFIG);
            }
        });

        mCloseElectronicLocksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.ELECTRONIC_LOCKS, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF,RelayConfig.OLD_CONFIG);
            }
        });

        mOpenGatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.GATING, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON,RelayConfig.OLD_CONFIG);
            }
        });

        mCloseGatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.GATING, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF,RelayConfig.OLD_CONFIG);
            }
        });

        mRestartCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.CAR, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RESET_TIME.DEFAULT_RESET_TIME_SEC,RelayConfig.OLD_CONFIG);
            }
        });

        mOpenCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.CAR, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON,RelayConfig.OLD_CONFIG);
            }
        });

        mCloseCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.CAR, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF,RelayConfig.OLD_CONFIG);
            }
        });

        mRestartOneMachineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.ONE_MACHINE, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RESET_TIME.DEFAULT_RESET_TIME_SEC,RelayConfig.OLD_CONFIG);
            }
        });

        mOpenOneMachineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.ONE_MACHINE, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON,RelayConfig.OLD_CONFIG);
            }
        });

        mCloseOneMachineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.ONE_MACHINE, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF,RelayConfig.OLD_CONFIG);
            }
        });


        mOpenLedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.LED, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON,RelayConfig.OLD_CONFIG);
            }
        });

        mCloseLedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.LED, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF,RelayConfig.OLD_CONFIG);
            }
        });

        mOpenFanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.FAN, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON,RelayConfig.OLD_CONFIG);
            }
        });

        mCloseFanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.FAN, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF,RelayConfig.OLD_CONFIG);
            }
        });

        mOpenCompressorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 旧版本:开压缩机 关继电器
                 */
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.COMPRESSOR, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF,RelayConfig.OLD_CONFIG);
            }
        });

        mCloseCompressorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 旧版本:关压缩机 开继电器
                 */
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.COMPRESSOR, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON,RelayConfig.OLD_CONFIG);
            }
        });
        /**
         * end:旧版继电器默认配置
         */

        /**
         * start:新版继电器默认配置
         */
        mNewOpenElectronicLocksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* int openElectronicLocksResetTimeSec=40;
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.ELECTRONIC_LOCKS,BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON,openElectronicLocksResetTimeSec);*/

                /**
                 * 应设备端要求 开-off-1秒
                 */
                int openElectronicLocksResetTimeSec = 1;
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.ELECTRONIC_LOCKS, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF, openElectronicLocksResetTimeSec,RelayConfig.NEW_CONFIG);
            }
        });

        mNewCloseElectronicLocksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.ELECTRONIC_LOCKS, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF,RelayConfig.NEW_CONFIG);
            }
        });

        mNewOpenGatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.GATING, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON,RelayConfig.NEW_CONFIG);
            }
        });

        mNewCloseGatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.GATING, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF,RelayConfig.NEW_CONFIG);
            }
        });

        mNewRestartCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.CAR, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RESET_TIME.DEFAULT_RESET_TIME_SEC,RelayConfig.NEW_CONFIG);
            }
        });

        mNewOpenCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.CAR, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON,RelayConfig.NEW_CONFIG);
            }
        });

        mNewCloseCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.CAR, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF,RelayConfig.NEW_CONFIG);
            }
        });


        mNewRestartOneMachineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.ONE_MACHINE, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RESET_TIME.DEFAULT_RESET_TIME_SEC,RelayConfig.NEW_CONFIG);
            }
        });

        mNewOpenOneMachineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.ONE_MACHINE, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON,RelayConfig.NEW_CONFIG);
            }
        });

        mNewCloseOneMachineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.ONE_MACHINE, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF,RelayConfig.NEW_CONFIG);
            }
        });


        mNewOpenLedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.LED, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON,RelayConfig.NEW_CONFIG);
            }
        });

        mNewCloseLedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.LED, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF,RelayConfig.NEW_CONFIG);
            }
        });

        mNewOpenFanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.FAN, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON, RelayConfig.NEW_CONFIG);
            }
        });

        mNewCloseFanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.FAN, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF,RelayConfig.NEW_CONFIG);
            }
        });

        mNewOpenCompressorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 新版本:开压缩机 关继电器
                 */
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.COMPRESSOR, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF,RelayConfig.NEW_CONFIG);
            }
        });

        mNewCloseCompressorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 新版本:关压缩机 开继电器
                 */
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.COMPRESSOR, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON,RelayConfig.NEW_CONFIG);
            }
        });


        mNewOpenHeatedGlassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.HEATED_GLASS, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON,RelayConfig.NEW_CONFIG);
            }
        });

        mNewCloseHeatedGlassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingRelay(BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.HEATED_GLASS, BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF,RelayConfig.NEW_CONFIG);
            }
        });
        /**
         * end:新版继电器默认配置
         */
    }



    /**
     * 支付宝摄像头测试
     */
    private void aliCameraTest() {
       if (CameraUtil.checkCameraAvaliable(getContext())) {
            Intent thumbIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            this.getActivity().startActivityForResult(thumbIntent, 1);
        } else {
            CustomToast.makeText(this.getContext(), "没有检测到摄像头!!", R.mipmap.sad, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 读取出货口高度
     */
    private void readOutGoodsPortHeight() {
        CustomToast.makeText(getContext(), getString(R.string.reading_out_goods_port_height), R.mipmap.smail, Toast.LENGTH_SHORT).show();
        Observable.create(new Observable.OnSubscribe<RspRwPopVal>() {
            @Override
            public void call(Subscriber<? super RspRwPopVal> subscriber) {
                byte        opcode      = BmtMsgConstant.DATA.REQ_RW_POP_VAL.CONSTANT.OPCODE.READ;
                ReqRwPopVal reqRwPopVal = new ReqRwPopVal(opcode, 0);

                RspRwPopVal rspRwPopVal = BmtVendingMachineUtil.getInstance().createCommand4ReqRwPopValBySync(reqRwPopVal);
                subscriber.onNext(rspRwPopVal);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<RspRwPopVal>() {
                    @Override
                    public void call(RspRwPopVal rspRwPopVal) {
                        if (null != rspRwPopVal) {
                            refreshOutGoodsPortHeightEt(rspRwPopVal.getPopValValue());
                            CustomToast.makeText(getContext(), getText(R.string.read_out_goods_port_height_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void writeOutGoodsPortHeight() {
        String tips = null;
        do {
            String outGoodsPortHeightStr = mOutGoodsPortHeightEt.getText().toString();
            if (CommonUtil.isEmpty(outGoodsPortHeightStr)) {
                tips = getString(R.string.out_goods_port_height_can_not_be_empty);
                break;
            }

            final int outGoodsPortHeight = Integer.valueOf(outGoodsPortHeightStr);

            if (outGoodsPortHeight <= 0) {
                tips = getString(R.string.out_goods_port_height_can_not_be_less_than_zero);
                break;
            }

            CustomToast.makeText(getContext(), getString(R.string.writing_out_goods_port_height), R.mipmap.smail, Toast.LENGTH_SHORT).show();
            Observable.create(new Observable.OnSubscribe<RspRwPopVal>() {
                @Override
                public void call(Subscriber<? super RspRwPopVal> subscriber) {
                    byte opcode = BmtMsgConstant.DATA.REQ_RW_POP_VAL.CONSTANT.OPCODE.WRITE;
                    ReqRwPopVal reqRwPopVal = new ReqRwPopVal(opcode, outGoodsPortHeight);
                    RspRwPopVal rspRwPopVal = BmtVendingMachineUtil.getInstance().createCommand4ReqRwPopValBySync(reqRwPopVal);
                    subscriber.onNext(rspRwPopVal);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                    .subscribe(new Action1<RspRwPopVal>() {
                        @Override
                        public void call(RspRwPopVal rspRwPopVal) {
                            if (null != rspRwPopVal) {
                                refreshOutGoodsPortHeightEt(rspRwPopVal.getPopValValue());
                                CustomToast.makeText(getContext(), getText(R.string.write_out_goods_port_height_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } while (false);

        if (null != tips) {
            CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
        }
    }

    private void readPickupHeightCompensationValue() {
        CustomToast.makeText(getContext(), getString(R.string.reading_pickup_height_compensation_value), R.mipmap.smail, Toast.LENGTH_SHORT).show();
        Observable.create(new Observable.OnSubscribe<RspRwPickOffset>() {
            @Override
            public void call(Subscriber<? super RspRwPickOffset> subscriber) {
                byte            opcode          = BmtMsgConstant.DATA.REQ_RW_POP_VAL.CONSTANT.OPCODE.READ;
                ReqRwPickOffset reqRwPickOffset = new ReqRwPickOffset(opcode, (byte) 0);
                RspRwPickOffset rspRwPickOffset = BmtVendingMachineUtil.getInstance().createCommand4ReqRwPickOffsetBySync(reqRwPickOffset);
                subscriber.onNext(rspRwPickOffset);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<RspRwPickOffset>() {
                    @Override
                    public void call(RspRwPickOffset rspRwPickOffset) {
                        if (null != rspRwPickOffset) {
                            refreshPickupHeightCompensationValueEt(rspRwPickOffset.getCompensationValue());
                            CustomToast.makeText(getContext(), getText(R.string.read_pickup_height_compensation_value_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void writePickupHeightCompensationValue() {
        String tips = null;
        do {
            String pickupHeightCompensationValueStr = mPickupHeightCompensationValueEt.getText().toString();
            if (CommonUtil.isEmpty(pickupHeightCompensationValueStr)) {
                tips = getString(R.string.pickup_height_compensation_value_can_not_be_empty);
                break;
            }

            final int pickupHeightCompensationValue = Integer.valueOf(pickupHeightCompensationValueStr);
            CustomToast.makeText(getContext(), getString(R.string.writing_pickup_height_compensation_value), R.mipmap.smail, Toast.LENGTH_SHORT).show();
            Observable.create(new Observable.OnSubscribe<RspRwPickOffset>() {
                @Override
                public void call(Subscriber<? super RspRwPickOffset> subscriber) {
                    byte opcode = BmtMsgConstant.DATA.REQ_RW_POP_VAL.CONSTANT.OPCODE.WRITE;
                    ReqRwPickOffset reqRwPickOffset = new ReqRwPickOffset(opcode, (byte) pickupHeightCompensationValue);
                    RspRwPickOffset rspRwPickOffset = BmtVendingMachineUtil.getInstance().createCommand4ReqRwPickOffsetBySync(reqRwPickOffset);
                    subscriber.onNext(rspRwPickOffset);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                    .subscribe(new Action1<RspRwPickOffset>() {
                        @Override
                        public void call(RspRwPickOffset rspRwPickOffset) {
                            if (null != rspRwPickOffset) {
                                refreshPickupHeightCompensationValueEt(rspRwPickOffset.getCompensationValue());
                                CustomToast.makeText(getContext(), getText(R.string.write_pickup_height_compensation_value_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } while (false);

        if (null != tips) {
            CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
        }
    }

    private void readXAxisMotorStartingSpeed() {
        CustomToast.makeText(getContext(), getString(R.string.reading_x_axis_motor_starting_speed), R.mipmap.smail, Toast.LENGTH_SHORT).show();
        Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
            @Override
            public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                byte                 slaverId             = CommandDef.FUNC_SLAVER_ID_MCS;
                byte                 cmd                  = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                byte                 para                 = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.READ;
                ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.X_AXIS_MOTOR_STARTING_SPEED);
                RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                subscriber.onNext(rspRwSystemParameter);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<RspRwSystemParameter>() {
                    @Override
                    public void call(RspRwSystemParameter rspRwSystemParameter) {
                        if (null != rspRwSystemParameter) {
                            refreshXAxisMotorStartingSpeedEt(rspRwSystemParameter.getParaValueOfInt());
                            CustomToast.makeText(getContext(), getText(R.string.read_x_axis_motor_starting_speed_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void writeXAxisMotorStartingSpeed() {
        String tips = null;
        do {
            String xAxisMotorStartingSpeedStr = mXAxisMotorStartingSpeedEt.getText().toString();
            if (CommonUtil.isEmpty(xAxisMotorStartingSpeedStr)) {
                tips = getString(R.string.x_axis_motor_starting_speed_can_not_be_empty);
                break;
            }

            final int xAxisMotorStartingSpeed = Integer.valueOf(xAxisMotorStartingSpeedStr);
            if (xAxisMotorStartingSpeed <= 0) {
                tips = getString(R.string.x_axis_motor_starting_speed_can_not_be_less_than_zero);
                break;
            }

            CustomToast.makeText(getContext(), getString(R.string.writing_x_axis_motor_starting_speed), R.mipmap.smail, Toast.LENGTH_SHORT).show();
            Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
                @Override
                public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                    byte slaverId = CommandDef.FUNC_SLAVER_ID_MCS;
                    byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                    byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.WRITE;
                    ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.X_AXIS_MOTOR_STARTING_SPEED, xAxisMotorStartingSpeed);
                    RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                    subscriber.onNext(rspRwSystemParameter);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                    .subscribe(new Action1<RspRwSystemParameter>() {
                        @Override
                        public void call(RspRwSystemParameter rspRwSystemParameter) {
                            if (null != rspRwSystemParameter) {
                                refreshXAxisMotorStartingSpeedEt(rspRwSystemParameter.getParaValueOfInt());
                                CustomToast.makeText(getContext(), getText(R.string.write_x_axis_motor_starting_speed_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } while (false);

        if (null != tips) {
            CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
        }
    }

    private void readXAxisMotorMaximumSpeed() {
        CustomToast.makeText(getContext(), getString(R.string.reading_x_axis_motor_maximum_speed), R.mipmap.smail, Toast.LENGTH_SHORT).show();
        Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
            @Override
            public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                byte slaverId = CommandDef.FUNC_SLAVER_ID_MCS;
                byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.READ;
                ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.X_AXIS_MOTOR_MAXIMUM_SPEED);
                RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                subscriber.onNext(rspRwSystemParameter);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<RspRwSystemParameter>() {
                    @Override
                    public void call(RspRwSystemParameter rspRwSystemParameter) {
                        if (null != rspRwSystemParameter) {
                            refreshXAxisMotorMaximumSpeedEt(rspRwSystemParameter.getParaValueOfInt());
                            CustomToast.makeText(getContext(), getText(R.string.read_x_axis_motor_maximum_speed_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void writeXAxisMotorMaximumSpeed() {
        String tips = null;
        do {
            String xAxisMotorMaximumSpeedStr = mXAxisMotorMaximumSpeedEt.getText().toString();
            if (CommonUtil.isEmpty(xAxisMotorMaximumSpeedStr)) {
                tips = getString(R.string.x_axis_motor_maximum_speed_can_not_be_empty);
                break;
            }

            final int xAxisMotorMaximumSpeed = Integer.valueOf(xAxisMotorMaximumSpeedStr);
            if (xAxisMotorMaximumSpeed <= 0) {
                tips = getString(R.string.x_axis_motor_maximum_speed_can_not_be_less_than_zero);
                break;
            }

            CustomToast.makeText(getContext(), getString(R.string.writing_x_axis_motor_maximum_speed), R.mipmap.smail, Toast.LENGTH_SHORT).show();
            Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
                @Override
                public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                    byte slaverId = CommandDef.FUNC_SLAVER_ID_MCS;
                    byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                    byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.WRITE;
                    ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.X_AXIS_MOTOR_MAXIMUM_SPEED, xAxisMotorMaximumSpeed);
                    RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                    subscriber.onNext(rspRwSystemParameter);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                    .subscribe(new Action1<RspRwSystemParameter>() {
                        @Override
                        public void call(RspRwSystemParameter rspRwSystemParameter) {
                            if (null != rspRwSystemParameter) {
                                refreshXAxisMotorMaximumSpeedEt(rspRwSystemParameter.getParaValueOfInt());
                                CustomToast.makeText(getContext(), getText(R.string.write_x_axis_motor_maximum_speed_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } while (false);

        if (null != tips) {
            CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
        }
    }

    private void readXAxisMotorAccelerationDistance() {
        CustomToast.makeText(getContext(), getString(R.string.reading_x_axis_motor_acceleration_distance), R.mipmap.smail, Toast.LENGTH_SHORT).show();
        Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
            @Override
            public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                byte slaverId = CommandDef.FUNC_SLAVER_ID_MCS;
                byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.READ;
                ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.X_AXIS_MOTOR_ACCELERATION_DISTANCE);
                RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                subscriber.onNext(rspRwSystemParameter);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<RspRwSystemParameter>() {
                    @Override
                    public void call(RspRwSystemParameter rspRwSystemParameter) {
                        if (null != rspRwSystemParameter) {
                            refreshXAxisMotorAccelerationDistanceEt(rspRwSystemParameter.getParaValueOfInt());
                            CustomToast.makeText(getContext(), getText(R.string.read_x_axis_motor_acceleration_distance_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void writeXAxisMotorAccelerationDistance() {
        String tips = null;
        do {
            String xAxisMotorAccelerationDistanceStr = mXAxisMotorAccelerationDistanceEt.getText().toString();
            if (CommonUtil.isEmpty(xAxisMotorAccelerationDistanceStr)) {
                tips = getString(R.string.x_axis_motor_acceleration_distance_can_not_be_empty);
                break;
            }

            final int xAxisMotorAccelerationDistance = Integer.valueOf(xAxisMotorAccelerationDistanceStr);
            if (xAxisMotorAccelerationDistance <= 0) {
                tips = getString(R.string.x_axis_motor_acceleration_distance_can_not_be_less_than_zero);
                break;
            }

            CustomToast.makeText(getContext(), getString(R.string.writing_x_axis_motor_acceleration_distance), R.mipmap.smail, Toast.LENGTH_SHORT).show();
            Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
                @Override
                public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                    byte slaverId = CommandDef.FUNC_SLAVER_ID_MCS;
                    byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                    byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.WRITE;
                    ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.X_AXIS_MOTOR_ACCELERATION_DISTANCE, xAxisMotorAccelerationDistance);
                    RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                    subscriber.onNext(rspRwSystemParameter);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                    .subscribe(new Action1<RspRwSystemParameter>() {
                        @Override
                        public void call(RspRwSystemParameter rspRwSystemParameter) {
                            if (null != rspRwSystemParameter) {
                                refreshXAxisMotorAccelerationDistanceEt(rspRwSystemParameter.getParaValueOfInt());
                                CustomToast.makeText(getContext(), getText(R.string.write_x_axis_motor_acceleration_distance_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } while (false);

        if (null != tips) {
            CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
        }
    }

    private void readYAxisMotorStartingSpeed() {
        CustomToast.makeText(getContext(), getString(R.string.reading_y_axis_motor_starting_speed), R.mipmap.smail, Toast.LENGTH_SHORT).show();
        Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
            @Override
            public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                byte slaverId = CommandDef.FUNC_SLAVER_ID_MCS;
                byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.READ;
                ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.Y_AXIS_MOTOR_STARTING_SPEED);
                RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                subscriber.onNext(rspRwSystemParameter);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<RspRwSystemParameter>() {
                    @Override
                    public void call(RspRwSystemParameter rspRwSystemParameter) {
                        if (null != rspRwSystemParameter) {
                            refreshYAxisMotorStartingSpeedEt(rspRwSystemParameter.getParaValueOfInt());
                            CustomToast.makeText(getContext(), getText(R.string.read_y_axis_motor_starting_speed_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void writeYAxisMotorStartingSpeed() {
        String tips = null;
        do {
            String yAxisMotorStartingSpeedStr = mYAxisMotorStartingSpeedEt.getText().toString();
            if (CommonUtil.isEmpty(yAxisMotorStartingSpeedStr)) {
                tips = getString(R.string.y_axis_motor_starting_speed_can_not_be_empty);
                break;
            }

            final int yAxisMotorStartingSpeed = Integer.valueOf(yAxisMotorStartingSpeedStr);
            if (yAxisMotorStartingSpeed <= 0) {
                tips = getString(R.string.y_axis_motor_starting_speed_can_not_be_less_than_zero);
                break;
            }

            CustomToast.makeText(getContext(), getString(R.string.writing_y_axis_motor_starting_speed), R.mipmap.smail, Toast.LENGTH_SHORT).show();
            Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
                @Override
                public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                    byte slaverId = CommandDef.FUNC_SLAVER_ID_MCS;
                    byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                    byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.WRITE;
                    ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.Y_AXIS_MOTOR_STARTING_SPEED, yAxisMotorStartingSpeed);
                    RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                    subscriber.onNext(rspRwSystemParameter);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                    .subscribe(new Action1<RspRwSystemParameter>() {
                        @Override
                        public void call(RspRwSystemParameter rspRwSystemParameter) {
                            if (null != rspRwSystemParameter) {
                                refreshYAxisMotorStartingSpeedEt(rspRwSystemParameter.getParaValueOfInt());
                                CustomToast.makeText(getContext(), getText(R.string.write_y_axis_motor_starting_speed_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } while (false);

        if (null != tips) {
            CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
        }
    }

    private void readYAxisMotorMaximumSpeed() {
        CustomToast.makeText(getContext(), getString(R.string.reading_y_axis_motor_maximum_speed), R.mipmap.smail, Toast.LENGTH_SHORT).show();
        Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
            @Override
            public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                byte slaverId = CommandDef.FUNC_SLAVER_ID_MCS;
                byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.READ;
                ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.Y_AXIS_MOTOR_MAXIMUM_SPEED);
                RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                subscriber.onNext(rspRwSystemParameter);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<RspRwSystemParameter>() {
                    @Override
                    public void call(RspRwSystemParameter rspRwSystemParameter) {
                        if (null != rspRwSystemParameter) {
                            refreshYAxisMotorMaximumSpeedEt(rspRwSystemParameter.getParaValueOfInt());
                            CustomToast.makeText(getContext(), getText(R.string.read_y_axis_motor_maximum_speed_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void writeYAxisMotorMaximumSpeed() {
        String tips = null;
        do {
            String yAxisMotorMaximumSpeedStr = mYAxisMotorMaximumSpeedEt.getText().toString();
            if (CommonUtil.isEmpty(yAxisMotorMaximumSpeedStr)) {
                tips = getString(R.string.y_axis_motor_maximum_speed_can_not_be_empty);
                break;
            }

            final int yAxisMotorMaximumSpeed = Integer.valueOf(yAxisMotorMaximumSpeedStr);
            if (yAxisMotorMaximumSpeed <= 0) {
                tips = getString(R.string.y_axis_motor_maximum_speed_can_not_be_less_than_zero);
                break;
            }

            CustomToast.makeText(getContext(), getString(R.string.writing_y_axis_motor_maximum_speed), R.mipmap.smail, Toast.LENGTH_SHORT).show();
            Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
                @Override
                public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                    byte slaverId = CommandDef.FUNC_SLAVER_ID_MCS;
                    byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                    byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.WRITE;
                    ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.Y_AXIS_MOTOR_MAXIMUM_SPEED, yAxisMotorMaximumSpeed);
                    RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                    subscriber.onNext(rspRwSystemParameter);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                    .subscribe(new Action1<RspRwSystemParameter>() {
                        @Override
                        public void call(RspRwSystemParameter rspRwSystemParameter) {
                            if (null != rspRwSystemParameter) {
                                refreshYAxisMotorMaximumSpeedEt(rspRwSystemParameter.getParaValueOfInt());
                                CustomToast.makeText(getContext(), getText(R.string.write_y_axis_motor_maximum_speed_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } while (false);

        if (null != tips) {
            CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
        }
    }

    private void readYAxisMotorAccelerationDistance() {
        CustomToast.makeText(getContext(), getString(R.string.reading_y_axis_motor_acceleration_distance), R.mipmap.smail, Toast.LENGTH_SHORT).show();
        Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
            @Override
            public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                byte slaverId = CommandDef.FUNC_SLAVER_ID_MCS;
                byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.READ;
                ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.Y_AXIS_MOTOR_ACCELERATION_DISTANCE);
                RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                subscriber.onNext(rspRwSystemParameter);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<RspRwSystemParameter>() {
                    @Override
                    public void call(RspRwSystemParameter rspRwSystemParameter) {
                        if (null != rspRwSystemParameter) {
                            refreshYAxisMotorAccelerationDistanceEt(rspRwSystemParameter.getParaValueOfInt());
                            CustomToast.makeText(getContext(), getText(R.string.read_y_axis_motor_acceleration_distance_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void writeYAxisMotorAccelerationDistance() {
        String tips = null;
        do {
            String yAxisMotorAccelerationDistanceStr = mYAxisMotorAccelerationDistanceEt.getText().toString();
            if (CommonUtil.isEmpty(yAxisMotorAccelerationDistanceStr)) {
                tips = getString(R.string.y_axis_motor_acceleration_distance_can_not_be_empty);
                break;
            }

            final int yAxisMotorAccelerationDistance = Integer.valueOf(yAxisMotorAccelerationDistanceStr);
            if (yAxisMotorAccelerationDistance <= 0) {
                tips = getString(R.string.y_axis_motor_acceleration_distance_can_not_be_less_than_zero);
                break;
            }

            CustomToast.makeText(getContext(), getString(R.string.writing_y_axis_motor_acceleration_distance), R.mipmap.smail, Toast.LENGTH_SHORT).show();
            Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
                @Override
                public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                    byte slaverId = CommandDef.FUNC_SLAVER_ID_MCS;
                    byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                    byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.WRITE;
                    ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.Y_AXIS_MOTOR_ACCELERATION_DISTANCE, yAxisMotorAccelerationDistance);
                    RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                    subscriber.onNext(rspRwSystemParameter);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                    .subscribe(new Action1<RspRwSystemParameter>() {
                        @Override
                        public void call(RspRwSystemParameter rspRwSystemParameter) {
                            if (null != rspRwSystemParameter) {
                                refreshYAxisMotorAccelerationDistanceEt(rspRwSystemParameter.getParaValueOfInt());
                                CustomToast.makeText(getContext(), getText(R.string.write_y_axis_motor_acceleration_distance_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } while (false);

        if (null != tips) {
            CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
        }
    }

    private void readCarWheelCircumference() {
        CustomToast.makeText(getContext(), getString(R.string.reading_car_wheel_circumference), R.mipmap.smail, Toast.LENGTH_SHORT).show();
        Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
            @Override
            public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                byte slaverId = CommandDef.FUNC_SLAVER_ID_MCS;
                byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.READ;
                ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.CAR_WHEEL_CIRCUMFERENCE);
                RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                subscriber.onNext(rspRwSystemParameter);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<RspRwSystemParameter>() {
                    @Override
                    public void call(RspRwSystemParameter rspRwSystemParameter) {
                        if (null != rspRwSystemParameter) {
                            refreshCarWheelCircumferenceEt(rspRwSystemParameter.getParaValueOfInt());
                            CustomToast.makeText(getContext(), getText(R.string.read_car_wheel_circumference_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void writeCarWheelCircumference() {
        String tips = null;
        do {
            String carWheelCircumferenceStr = mCarWheelCircumferenceEt.getText().toString();
            if (CommonUtil.isEmpty(carWheelCircumferenceStr)) {
                tips = getString(R.string.car_wheel_circumference_can_not_be_empty);
                break;
            }

            final int carWheelCircumference = Integer.valueOf(carWheelCircumferenceStr);
            if (carWheelCircumference <= 0) {
                tips = getString(R.string.car_wheel_circumference_can_not_be_less_than_zero);
                break;
            }

            CustomToast.makeText(getContext(), getString(R.string.writing_car_wheel_circumference), R.mipmap.smail, Toast.LENGTH_SHORT).show();
            Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
                @Override
                public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                    byte slaverId = CommandDef.FUNC_SLAVER_ID_MCS;
                    byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                    byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.WRITE;
                    ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.CAR_WHEEL_CIRCUMFERENCE, carWheelCircumference);
                    RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                    subscriber.onNext(rspRwSystemParameter);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                    .subscribe(new Action1<RspRwSystemParameter>() {
                        @Override
                        public void call(RspRwSystemParameter rspRwSystemParameter) {
                            if (null != rspRwSystemParameter) {
                                refreshCarWheelCircumferenceEt(rspRwSystemParameter.getParaValueOfInt());
                                CustomToast.makeText(getContext(), getText(R.string.write_car_wheel_circumference_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } while (false);

        if (null != tips) {
            CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
        }
    }

    private void readLiftingSynchronousWheelCircumference() {
        CustomToast.makeText(getContext(), getString(R.string.reading_lifting_synchronous_wheel_circumference), R.mipmap.smail, Toast.LENGTH_SHORT).show();
        Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
            @Override
            public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                byte slaverId = CommandDef.FUNC_SLAVER_ID_MCS;
                byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.READ;
                ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.LIFTING_SYNCHRONOUS_WHEEL_CIRCUMFERENCE);
                RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                subscriber.onNext(rspRwSystemParameter);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<RspRwSystemParameter>() {
                    @Override
                    public void call(RspRwSystemParameter rspRwSystemParameter) {
                        if (null != rspRwSystemParameter) {
                            refreshLiftingSynchronousWheelCircumferenceEt(rspRwSystemParameter.getParaValueOfInt());
                            CustomToast.makeText(getContext(), getText(R.string.read_lifting_synchronous_wheel_circumference_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void writeLiftingSynchronousWheelCircumference() {
        String tips = null;
        do {
            String liftingSynchronousWheelCircumferenceStr = mLiftingSynchronousWheelCircumferenceEt.getText().toString();
            if (CommonUtil.isEmpty(liftingSynchronousWheelCircumferenceStr)) {
                tips = getString(R.string.lifting_synchronous_wheel_circumference_can_not_be_empty);
                break;
            }

            final int liftingSynchronousWheelCircumference = Integer.valueOf(liftingSynchronousWheelCircumferenceStr);
            if (liftingSynchronousWheelCircumference <= 0) {
                tips = getString(R.string.lifting_synchronous_wheel_circumference_can_not_be_less_than_zero);
                break;
            }

            CustomToast.makeText(getContext(), getString(R.string.writing_lifting_synchronous_wheel_circumference), R.mipmap.smail, Toast.LENGTH_SHORT).show();
            Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
                @Override
                public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                    byte slaverId = CommandDef.FUNC_SLAVER_ID_MCS;
                    byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                    byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.WRITE;
                    ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsMcsConstant.IPS_MCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.LIFTING_SYNCHRONOUS_WHEEL_CIRCUMFERENCE, liftingSynchronousWheelCircumference);
                    RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                    subscriber.onNext(rspRwSystemParameter);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                    .subscribe(new Action1<RspRwSystemParameter>() {
                        @Override
                        public void call(RspRwSystemParameter rspRwSystemParameter) {
                            if (null != rspRwSystemParameter) {
                                refreshLiftingSynchronousWheelCircumferenceEt(rspRwSystemParameter.getParaValueOfInt());
                                CustomToast.makeText(getContext(), getText(R.string.write_lifting_synchronous_wheel_circumference_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } while (false);

        if (null != tips) {
            CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
        }
    }

    private void readSliderSynchronizationWheelCircumference() {
        CustomToast.makeText(getContext(), getString(R.string.reading_slider_synchronization_wheel_circumference), R.mipmap.smail, Toast.LENGTH_SHORT).show();
        Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
            @Override
            public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                byte slaverId = CommandDef.FUNC_SLAVER_ID_PCS;
                byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.READ;
                ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsPcsConstant.IPS_PCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.SLIDER_SYNCHRONIZATION_WHEEL_CIRCUMFERENCE);
                RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                subscriber.onNext(rspRwSystemParameter);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<RspRwSystemParameter>() {
                    @Override
                    public void call(RspRwSystemParameter rspRwSystemParameter) {
                        if (null != rspRwSystemParameter) {
                            refreshSliderSynchronizationWheelCircumferenceEt(rspRwSystemParameter.getParaValueOfInt());
                            CustomToast.makeText(getContext(), getText(R.string.read_slider_synchronization_wheel_circumference_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void writeSliderSynchronizationWheelCircumference() {
        String tips = null;
        do {
            String sliderSynchronizationWheelCircumferenceStr = mSliderSynchronizationWheelCircumferenceEt.getText().toString();
            if (CommonUtil.isEmpty(sliderSynchronizationWheelCircumferenceStr)) {
                tips = getString(R.string.slider_synchronization_wheel_circumference_can_not_be_empty);
                break;
            }

            final int sliderSynchronizationWheelCircumference = Integer.valueOf(sliderSynchronizationWheelCircumferenceStr);
            if (sliderSynchronizationWheelCircumference <= 0) {
                tips = getString(R.string.slider_synchronization_wheel_circumference_can_not_be_less_than_zero);
                break;
            }

            CustomToast.makeText(getContext(), getString(R.string.writing_slider_synchronization_wheel_circumference), R.mipmap.smail, Toast.LENGTH_SHORT).show();
            Observable.create(new Observable.OnSubscribe<RspRwSystemParameter>() {
                @Override
                public void call(Subscriber<? super RspRwSystemParameter> subscriber) {
                    byte slaverId = CommandDef.FUNC_SLAVER_ID_PCS;
                    byte cmd = IpsConstant.MSG_CONSTANT.FUNC.CMD.R_W_SYSTEM_PARAMETER;
                    byte para = IpsConstant.MSG_CONSTANT.FUNC.PARA.R_W_SYSTEM_PARAMETER.WRITE;
                    ReqRwSystemParameter reqRwSystemParameter = new ReqRwSystemParameter(IpsPcsConstant.IPS_PCS_MSG.REQ_R_W_SYSTEM_PARAMETER.CONSTANT.DATA.INDEX.SLIDER_SYNCHRONIZATION_WHEEL_CIRCUMFERENCE, sliderSynchronizationWheelCircumference);
                    RspRwSystemParameter rspRwSystemParameter = BmtVendingMachineUtil.getInstance().createCommand4ReqRwSystemParameterBySync(slaverId, cmd, para, reqRwSystemParameter);
                    subscriber.onNext(rspRwSystemParameter);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                    .subscribe(new Action1<RspRwSystemParameter>() {
                        @Override
                        public void call(RspRwSystemParameter rspRwSystemParameter) {
                            if (null != rspRwSystemParameter) {
                                refreshSliderSynchronizationWheelCircumferenceEt(rspRwSystemParameter.getParaValueOfInt());
                                CustomToast.makeText(getContext(), getText(R.string.write_slider_synchronization_wheel_circumference_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } while (false);

        if (null != tips) {
            CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
        }
    }

    private void readTemp() {
        CustomToast.makeText(getContext(), getString(R.string.reading_temp), R.mipmap.smail, Toast.LENGTH_SHORT).show();
        String tips = null;
        do {
            String tempAreaIndexStr = mTempAreaIndexEt.getText().toString();
            if (CommonUtil.isEmpty(tempAreaIndexStr)) {
                tips = getString(R.string.temp_area_index_can_not_be_empty);
                break;
            }

            final int tempAreaIndex = Integer.valueOf(tempAreaIndexStr);
            Observable.create(new Observable.OnSubscribe<List<RspTempHumidityControl>>() {
                @Override
                public void call(Subscriber<? super List<RspTempHumidityControl>> subscriber) {
                    List<ReqTempHumidityControl> reqTempHumidityControls = new ArrayList<>();
                    ReqTempHumidityControl reqTempHumidityControl  = new ReqTempHumidityControl();
                    reqTempHumidityControl.setOpcode((byte) BmtMsgConstant.DATA.REQ_TEMP_HUMIDITY_CONTROL.CONSTANT.OPCODE.READ);
                    reqTempHumidityControl.setAreaIndex((byte) tempAreaIndex);
                    reqTempHumidityControl.setTempValue((byte) BmtMsgConstant.DATA.REQ_TEMP_HUMIDITY_CONTROL.CONSTANT.TEMP_VALUE.DEFAULT_TEMP_VALUE);
                    reqTempHumidityControls.add(reqTempHumidityControl);

                    List<RspTempHumidityControl> rspTempHumidityControls = BmtVendingMachineUtil.getInstance().createCommand4AllTempControl(reqTempHumidityControls);
                    subscriber.onNext(rspTempHumidityControls);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                    .subscribe(new Action1<List<RspTempHumidityControl>>() {
                        @Override
                        public void call(List<RspTempHumidityControl> rspTempHumidityControls) {
                            if (null != rspTempHumidityControls && rspTempHumidityControls.size() > 0) {
                                RspTempHumidityControl rspTempHumidityControl = rspTempHumidityControls.get(0);
                                //int temp=rspTempHumidityControl.getTempValue();
                                //refreshSettingTempEt(temp);
                                refreshSettingTempEt(rspTempHumidityControl.getSetTempValue2());
                                refreshActualTempTv(rspTempHumidityControl.getTempValue2());
                                CustomToast.makeText(getContext(), getText(R.string.read_temp_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } while (false);

        if (null != tips) {
            CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
        }
    }

    private void writeTemp() {
        String tips = null;
        do {

            String tempAreaIndexStr = mTempAreaIndexEt.getText().toString();
            if (CommonUtil.isEmpty(tempAreaIndexStr)) {
                tips = getString(R.string.temp_area_index_can_not_be_empty);
                break;
            }
            final int tempAreaIndex = Integer.valueOf(tempAreaIndexStr);

            String tempStr = mSettingTempEt.getText().toString();
            if (CommonUtil.isEmpty(tempStr)) {
                tips = getString(R.string.temp_can_not_be_empty);
                break;
            }

            final int temp = Integer.valueOf(tempStr);

            CustomToast.makeText(getContext(), getString(R.string.writing_temp), R.mipmap.smail, Toast.LENGTH_SHORT).show();
            Observable.create(new Observable.OnSubscribe<List<RspTempHumidityControl>>() {
                @Override
                public void call(Subscriber<? super List<RspTempHumidityControl>> subscriber) {
                    List<ReqTempHumidityControl> reqTempHumidityControls = new ArrayList<>();
                    ReqTempHumidityControl reqTempHumidityControl = new ReqTempHumidityControl();
                    reqTempHumidityControl.setOpcode((byte) BmtMsgConstant.DATA.REQ_TEMP_HUMIDITY_CONTROL.CONSTANT.OPCODE.WRITE);
                    reqTempHumidityControl.setAreaIndex((byte) tempAreaIndex);
                    reqTempHumidityControl.setTempValue((byte) temp);
                    reqTempHumidityControls.add(reqTempHumidityControl);

                    List<RspTempHumidityControl> rspTempHumidityControls = BmtVendingMachineUtil.getInstance().createCommand4AllTempControl(reqTempHumidityControls);
                    subscriber.onNext(rspTempHumidityControls);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                    .subscribe(new Action1<List<RspTempHumidityControl>>() {
                        @Override
                        public void call(List<RspTempHumidityControl> rspTempHumidityControls) {
                            if (null != rspTempHumidityControls && rspTempHumidityControls.size() > 0) {
                                RspTempHumidityControl rspTempHumidityControl = rspTempHumidityControls.get(0);
                                //int temp=rspTempHumidityControl.getTempValue();
                                refreshSettingTempEt(rspTempHumidityControl.getSetTempValue2());
                                refreshActualTempTv(rspTempHumidityControl.getTempValue2());
                                CustomToast.makeText(getContext(), getText(R.string.write_temp_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } while (false);

        if (null != tips) {
            CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
        }
    }


    private void getSensorState() {
        refreshSensorStateTv(null);
        CustomToast.makeText(getContext(), getString(R.string.getting_sensor_state), R.mipmap.smail, Toast.LENGTH_SHORT).show();
        Observable.create(new Observable.OnSubscribe<RspGetSensorState>() {
            @Override
            public void call(Subscriber<? super RspGetSensorState> subscriber) {

                RspGetSensorState rspGetSensorState = BmtVendingMachineUtil.getInstance().createCommand4GetSensorStateBySync();
                subscriber.onNext(rspGetSensorState);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<RspGetSensorState>() {
                    @Override
                    public void call(RspGetSensorState rspGetSensorState) {
                        if (null != rspGetSensorState) {
                            refreshSensorStateTv(rspGetSensorState);
                            CustomToast.makeText(getContext(), getText(R.string.get_sensor_state_success), R.mipmap.smail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    private void operatingRelay() {
        String tips = null;
        do {
            String areaIndexStr = mAreaIndexEt.getText().toString();
            if (CommonUtil.isEmpty(areaIndexStr)) {
                tips = "柜门号不能为空!";
                break;
            }

            final String relayIndexStr = mRelayIndexEt.getText().toString();
            if (CommonUtil.isEmpty(relayIndexStr)) {
                tips = "继电器号不能为空!";
                break;
            }
            String actionStr = mActionEt.getText().toString();
            if (CommonUtil.isEmpty(actionStr)) {
                tips = "动作码不能为空!";
                break;
            }

            String resetTimeStr = mResetTimeEt.getText().toString();
            if (CommonUtil.isEmpty(resetTimeStr)) {
                tips = "动作恢复时间不能为空!";
                break;
            }

            int areaIndex = Integer.valueOf(areaIndexStr);
            int relayIndex = Integer.valueOf(relayIndexStr);
            int action = Integer.valueOf(actionStr);
            int resetTime = Integer.valueOf(resetTimeStr);

            ReqRelayControl reqRelayControl = new ReqRelayControl();
            reqRelayControl.setAreaIndex((byte) areaIndex);
            reqRelayControl.setRelayIndex((byte) relayIndex);
            reqRelayControl.setAction((byte) action);
            reqRelayControl.setResetTime((byte) resetTime);

            switch (relayIndex) {
                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.RESERVED:
                    tips = getString(R.string.reserved);
                    break;
                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.ELECTRONIC_LOCKS:
                    tips = getString(R.string.electronic_locks);
                    break;
                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.GATING:
                    tips = getString(R.string.gating);
                    break;
                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.CAR:
                    tips = getString(R.string.car);
                    break;
                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.ONE_MACHINE:
                    tips = getString(R.string.one_machine);
                    break;
                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.LED:
                    tips = getString(R.string.led);
                    break;
                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.FAN:
                    tips = getString(R.string.fan);
                    break;
                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.COMPRESSOR:
                    tips = getString(R.string.compressor);
                    break;
            }

            switch (action) {
                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON:
                    tips = tips + "[开]";
                    break;
                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF:
                    tips = tips + "[关]";
                    break;
            }

            operatingRelay(reqRelayControl, tips);


        } while (false);

        if (null != tips) {
            CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
        }
        return;
    }

    private void operatingRelay(ReqRelayControl reqRelayControl, final String tips) {
        final List<ReqRelayControl> reqRelayControls = new ArrayList<>();
        reqRelayControls.add(reqRelayControl);
        operatingRelay(reqRelayControls, tips);
    }

    private void operatingRelay(final List<ReqRelayControl> reqRelayControls, final String tips) {
        CustomToast.makeText(getContext(), "执行" + tips + "中...", R.mipmap.smail, Toast.LENGTH_SHORT).show();
        Observable.create(new Observable.OnSubscribe<List<RspRelayControl>>() {
            @Override
            public void call(Subscriber<? super List<RspRelayControl>> subscriber) {
                List<RspRelayControl> rspRelayControls = BmtVendingMachineUtil.getInstance().createCommand4AllRelayControl(reqRelayControls);
                subscriber.onNext(rspRelayControls);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<List<RspRelayControl>>() {
                    @Override
                    public void call(List<RspRelayControl> rspRelayControls) {
                        if (null != rspRelayControls) {
                            if (rspRelayControls.size() > 0) {
                                RspRelayControl rspRelayControl = rspRelayControls.get(0);
                                boolean hasErrorCode = rspRelayControl.hasErrorCode();
                                if (hasErrorCode == false) {
                                    CustomToast.makeText(getContext(), "执行" + tips + "成功", R.mipmap.smail, Toast.LENGTH_SHORT).show();
                                } else {
                                    CustomToast.makeText(getContext(), "执行" + tips + "失败 错误码[" + DataUtil.bytesToHexString(rspRelayControl.getErrCode()) + "]", R.mipmap.smail, Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    }
                });

    }


    private void operatingRelay(int relayIndex, int action,int relayConfig) {
        do {
            String tips = Constant.NULL_STRING;
            String areaIndexStr = mCommonAreaIndexEt.getText().toString();
            if (CommonUtil.isEmpty(areaIndexStr)) {
                tips = "柜门号不能为空!";
                CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
                break;
            }

            final String resetTimeSecStr = mCommonResetTimeEt.getText().toString();
            if (CommonUtil.isEmpty(resetTimeSecStr)) {
                tips = "动作恢复时间不能为空!";
                CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
                break;
            }

            int areaIndex = Integer.valueOf(areaIndexStr);
            int resetTimeSec = Integer.valueOf(resetTimeSecStr);

            operatingRelay(areaIndex, relayIndex, action, resetTimeSec,relayConfig);


        } while (false);

    }

    private void operatingRelay(int relayIndex, int action, int resetTimeSec, int relayConfig) {
        do {
            String tips = Constant.NULL_STRING;
            String areaIndexStr = mCommonAreaIndexEt.getText().toString();
            if (CommonUtil.isEmpty(areaIndexStr)) {
                tips = "柜门号不能为空!";
                CustomToast.makeText(getContext(), tips, R.mipmap.sad, Toast.LENGTH_SHORT).show();
                break;
            }

            int areaIndex = Integer.valueOf(areaIndexStr);

            operatingRelay(areaIndex, relayIndex, action, resetTimeSec, relayConfig);


        } while (false);

    }

    private void operatingRelay(int areaIndex, int relayIndex, int action, int resetTimeSec, int relayConfig) {
        do {
            String tips = Constant.NULL_STRING;

            ReqRelayControl reqRelayControl = new ReqRelayControl();
            reqRelayControl.setAreaIndex((byte) areaIndex);
            reqRelayControl.setRelayIndex((byte) relayIndex);
            reqRelayControl.setAction((byte) action);
            reqRelayControl.setResetTime((byte) resetTimeSec);

            switch (action) {
                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON:
                    tips = tips + "[开]";
                    break;
                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF:
                    tips = tips + "[关]";
                    break;
            }
            switch (relayConfig) {
                case RelayConfig.NEW_CONFIG:
                    switch (relayIndex) {

                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.ELECTRONIC_LOCKS:
                            tips = getString(R.string.electronic_locks);
                            break;
                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.GATING:
                            tips = getString(R.string.gating);
                            break;
                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.CAR:
                            tips = getString(R.string.car);
                            break;
                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.ONE_MACHINE:
                            tips = getString(R.string.one_machine);
                            break;
                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.LED:
                            tips = getString(R.string.led);
                            break;
                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.FAN:
                            tips = getString(R.string.fan);
                            break;
                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.COMPRESSOR:
                            tips = getString(R.string.compressor);
                            /**
                             * 压缩机开关 对应继电器关开
                             */
                            switch (action) {
                                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF:
                                    tips = tips + "[开]";
                                    break;
                                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON:
                                    tips = tips + "[关]";
                                    break;
                            }
                            break;
                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX_OF_NEW.HEATED_GLASS:
                            tips = getString(R.string.heated_glass);
                            break;
                        default:
                            break;
                    }

                    break;
                default:
                    switch (relayIndex) {
                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.RESERVED:
                            tips = getString(R.string.reserved);
                            break;
                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.ELECTRONIC_LOCKS:
                            tips = getString(R.string.electronic_locks);
                            break;
                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.GATING:
                            tips = getString(R.string.gating);
                            break;
                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.CAR:
                            tips = getString(R.string.car);
                            break;
                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.ONE_MACHINE:
                            tips = getString(R.string.one_machine);
                            break;
                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.LED:
                            tips = getString(R.string.led);
                            break;
                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.FAN:
                            tips = getString(R.string.fan);
                            break;
                        case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.RELAY_INDEX.COMPRESSOR:
                            tips = getString(R.string.compressor);
                            /**
                             * 压缩机开关 对应继电器关开
                             */
                            switch (action) {
                                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.OFF:
                                    tips = tips + "[开]";
                                    break;
                                case BmtMsgConstant.DATA.REQ_RELAY_CONTROL.CONSTANT.ACTION.ON:
                                    tips = tips + "[关]";
                                    break;
                            }
                            break;
                        default:
                            break;
                    }

                    break;
            }


            operatingRelay(reqRelayControl, tips);
        } while (false);

        return;
    }



    private void refreshOutGoodsPortHeightEt(int value) {
        mOutGoodsPortHeightEt.setText(String.valueOf(value));
    }

    private void refreshPickupHeightCompensationValueEt(int value) {
        mPickupHeightCompensationValueEt.setText(String.valueOf(value));
    }


    private void refreshXAxisMotorStartingSpeedEt(int value) {
        mXAxisMotorStartingSpeedEt.setText(String.valueOf(value));
    }

    private void refreshXAxisMotorMaximumSpeedEt(int value) {
        mXAxisMotorMaximumSpeedEt.setText(String.valueOf(value));
    }

    private void refreshXAxisMotorAccelerationDistanceEt(int value) {
        mXAxisMotorAccelerationDistanceEt.setText(String.valueOf(value));
    }

    private void refreshYAxisMotorStartingSpeedEt(int value) {
        mYAxisMotorStartingSpeedEt.setText(String.valueOf(value));
    }

    private void refreshYAxisMotorMaximumSpeedEt(int value) {
        mYAxisMotorMaximumSpeedEt.setText(String.valueOf(value));
    }

    private void refreshYAxisMotorAccelerationDistanceEt(int value) {
        mYAxisMotorAccelerationDistanceEt.setText(String.valueOf(value));
    }

    private void refreshCarWheelCircumferenceEt(int value) {
        mCarWheelCircumferenceEt.setText(String.valueOf(value));
    }

    private void refreshLiftingSynchronousWheelCircumferenceEt(int value) {
        mLiftingSynchronousWheelCircumferenceEt.setText(String.valueOf(value));
    }

    private void refreshSliderSynchronizationWheelCircumferenceEt(int value) {
        mSliderSynchronizationWheelCircumferenceEt.setText(String.valueOf(value));
    }

    private void refreshSettingTempEt(int temp) {
        mSettingTempEt.setText(String.valueOf(temp));
    }

    private void refreshActualTempTv(int temp) {
        mActualTempTv.setText(String.valueOf(temp));
    }

    private void refreshSensorStateTv(RspGetSensorState rspGetSensorState) {
        if (null != rspGetSensorState) {
            mSensorStateTv.setText(rspGetSensorState.getAnalogySensorsDesc() + Constant.NEW_LINE + rspGetSensorState.getDigitalSensorsDesc());
            ViewUtil.showView(mSensorStateTv);
        } else {
            mSensorStateTv.setText(Constant.NULL);
            ViewUtil.hideView(mSensorStateTv);
        }
    }
}
