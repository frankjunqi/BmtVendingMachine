package com.bangmart.bmtvendingmachinedemo.helper;

import android.content.Context;
import android.text.TextUtils;

import com.bangmart.bmtvendingmachinedemo.androidboard.constant.AndroidBoardConstant;
import com.bangmart.bmtvendingmachinedemo.model.SerialPortInfo;
import com.bangmart.bmtvendingmachinedemo.util.CommonUtil;
import com.bangmart.bmtvendingmachinedemo.util.JsonUtil;
import com.bangmart.nt.channel.IChannelMonitor;
import com.bangmart.nt.command.Command;
import com.bangmart.nt.command.CommandDef;
import com.bangmart.nt.machine.HeartBeatListener;
import com.bangmart.nt.machine.Machine;
import com.bangmart.nt.machine.StateChangeListener;
import com.bangmart.nt.sys.ContextInfo;
import com.bangmart.nt.sys.LogToFile;
import com.bangmart.nt.sys.Logger;
import com.bangmart.nt.sys.Tools;
import com.bangmart.nt.vendingmachine.constant.BmtMsgConstant;
import com.bangmart.nt.vendingmachine.ipsmsg.model.IpsMsg;
import com.bangmart.nt.vendingmachine.ipsmsg.model.ReqRwSystemParameter;
import com.bangmart.nt.vendingmachine.ipsmsg.model.RspRwSystemParameter;
import com.bangmart.nt.vendingmachine.ipsmsg.util.IpsMsgUtil;
import com.bangmart.nt.vendingmachine.model.ReqRelayControl;
import com.bangmart.nt.vendingmachine.model.ReqRwPickOffset;
import com.bangmart.nt.vendingmachine.model.ReqRwPopVal;
import com.bangmart.nt.vendingmachine.model.ReqTempHumidityControl;
import com.bangmart.nt.vendingmachine.model.ReqUnitTest;
import com.bangmart.nt.vendingmachine.model.RspGetSensorState;
import com.bangmart.nt.vendingmachine.model.RspRelayControl;
import com.bangmart.nt.vendingmachine.model.RspRwPickOffset;
import com.bangmart.nt.vendingmachine.model.RspRwPopVal;
import com.bangmart.nt.vendingmachine.model.RspTempHumidityControl;
import com.bangmart.nt.vendingmachine.model.RspUnitTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoujq on 2018/1/8.
 * Bangmart自贩机帮助类
 */

public class BmtVendingMachineHelper {
    private static final String TAG = "BmtVendingMachineHelper";

    /**
     * 默认设备号长度
     */
    private static final int DEFAULT_DEVICE_NO_LENGTH = 10;
    /**
     * 默认设备号(10位)
     */
    public static final String DEFAULT_DEVICE_NO = "IamtheBmt!";
    private static BmtVendingMachineHelper sBmtVendingMachineHelper;
    private Context mContext;
    /**
     * 设备号
     */
    private String mDeviceNo = DEFAULT_DEVICE_NO;

    private Machine mMachine;
    private SerialPortInfo mSerialPortInfo;
    private int mComId;

    /**
     * 设备状态
     */
    private int mMachineStatus=Machine.STATE_UNKONWN;


    /**
     * start: Test 用于统计
     */

    private IChannelMonitor mIChannelMonitor =null;
    private HeartBeatListener mHeartBeatListener=null;
    private StateChangeListener mStateChangeListener=null;
    /**
     * end: Test 用于统计
     */


    public static BmtVendingMachineHelper getInstance(Context context, String deviceNo,SerialPortInfo serialPortInfo) {
        if (null == sBmtVendingMachineHelper) {
            sBmtVendingMachineHelper = new BmtVendingMachineHelper(context.getApplicationContext(), deviceNo,serialPortInfo);
        }


        return sBmtVendingMachineHelper;
    }

    public static void destoryInstance() {
        if (null != sBmtVendingMachineHelper) {
            sBmtVendingMachineHelper.freeBmtVendingMachineHelper();
            sBmtVendingMachineHelper = null;
        }

        return;
    }

    public Machine getMachine(){
        Machine machine=null;

        machine= findMachine();

        return machine;
    }


    private BmtVendingMachineHelper(Context context, String deviceNo,SerialPortInfo serialPortInfo) {
        mContext = context;
        mDeviceNo = deviceNo;
        mSerialPortInfo=serialPortInfo;
        initBmtVendingMachineHelper();
    }

    private void initBmtVendingMachineHelper() {
        /*********** 初始化数据缓存对象       *************/
  /*      dataCache = DataCache.getInstance();
        dataCache.loadFromDB();
        DBHelper.create(mContext);*/
        /*********** 初始化数据缓存对象       *************/

        /**
         * start:init log
         */
        //放到Application
  /*      ContextInfo.init(mContext);
        LogToFile.initialize(mContext);*/
        /**
         * end:init log
         */
        initMachine();

    }

    private void freeBmtVendingMachineHelper() {
    }

    public String getDeviceNo() {
        return mDeviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        mDeviceNo = deviceNo;
    }

    public int getMachineStatus() {
        return mMachineStatus;
    }

    public void setMachineStatus(int machineStatus) {
        mMachineStatus = machineStatus;
    }


    public IChannelMonitor getIChannelMonitor() {
        return mIChannelMonitor;
    }

    public void setIChannelMonitor(IChannelMonitor IChannelMonitor) {
        mIChannelMonitor = IChannelMonitor;
    }

    public HeartBeatListener getHeartBeatListener() {
        return mHeartBeatListener;
    }

    public void setHeartBeatListener(HeartBeatListener heartBeatListener) {
        mHeartBeatListener = heartBeatListener;
    }

    public StateChangeListener getStateChangeListener() {
        return mStateChangeListener;
    }

    public void setStateChangeListener(StateChangeListener stateChangeListener) {
        mStateChangeListener = stateChangeListener;
    }





    /**
     * 所有温度控制
     * @param reqTempHumidityControls
     */
    public List<RspTempHumidityControl> createCommand4AllTempControl(List<ReqTempHumidityControl> reqTempHumidityControls){
        List<RspTempHumidityControl> rspTempControls=new ArrayList<>();

        rspTempControls.addAll(createCommand4TempControl(reqTempHumidityControls));

        return rspTempControls;
    }

    /**
     * 温度控制
     * @param reqTempHumidityControls
     */
    public List<RspTempHumidityControl> createCommand4TempControl(List<ReqTempHumidityControl> reqTempHumidityControls){
        List<RspTempHumidityControl> rspTempControls=new ArrayList<>();
        do{
            Machine machine = findMachine();
            if (null == machine) {
                Logger.sLogger4Machine.info(TAG, "createCommand4TempControl null == machine mComId=" + mComId);
                break;
            }

            if(null==reqTempHumidityControls||reqTempHumidityControls.size()<=0){
                Logger.sLogger4Machine.info(TAG,"(null==reqTempControls||reqTempControls.size()<=0");
                break;
            }

            int reqTempControlsSize=reqTempHumidityControls.size();
            ReqTempHumidityControl reqTempControl=null;
            RspTempHumidityControl rspTempControl=null;
            for(int i=0; i<reqTempControlsSize; i++){
                reqTempControl=reqTempHumidityControls.get(i);
                byte[] reqData=ReqTempHumidityControl.valueOf(reqTempControl);

                Command cmd = Command.create(CommandDef.ID_TEMPERATURE_CONTROL,reqData, Command.TYPE_SYNC);
                machine.executeCommand(cmd);

                if (cmd.isError()){
                    printCmdErrorInfo(mComId,cmd);
                }
                {

                    if(null!=cmd.getResult()) {
                        byte[] rspData = cmd.getResult().getData();
                        if (null == rspData) {
                            Logger.sLogger4Machine.info(TAG, "createCommand4TempControl cmd.getResult().getData()==null");
                            break;
                        }
                        rspTempControl = RspTempHumidityControl.valueOf(rspData);
                        if (null != rspTempControl) {
                            rspTempControls.add(rspTempControl);
                        } else {
                            Logger.sLogger4Machine.info(TAG, "null == rspTempControl[" + Tools.bytesToHexString(rspData) + "]无法解析");
                        }
                    }else {
                        Logger.sLogger4Machine.info(TAG, "温度控制 cmd.getResult()==null");
                    }
                }
            }
        }while (false);

        return rspTempControls;
    }


    /**
     * 所有继电器控制
     * @param reqRelayControls
     * @return
     */
    public List<RspRelayControl> createCommand4AllRelayControl(List<ReqRelayControl> reqRelayControls){
        List<RspRelayControl> rspRelayControls=new ArrayList<>();

            rspRelayControls.addAll(createCommand4RelayControl(reqRelayControls));

        return rspRelayControls;
    }

    /**
     * 继电器控制
     * @param reqRelayControls
     * @return
     */
    public List<RspRelayControl> createCommand4RelayControl(List<ReqRelayControl> reqRelayControls){
        List<RspRelayControl> rspRelayControls=new ArrayList<>();
        do{
            Machine machine = findMachine();
            if (null == machine) {
                Logger.sLogger4Machine.info(TAG, "createCommand4RelayControl null == machine mComId=" + mComId);
                break;
            }

            if(null==reqRelayControls||reqRelayControls.size()<=0){
                Logger.sLogger4Machine.info(TAG,"(null==reqTempControls||reqTempControls.size()<=0");
                break;
            }

            int reqTempControlsSize=reqRelayControls.size();
            ReqRelayControl reqRelayControl=null;
            RspRelayControl rspRelayControl=null;
            for(int i=0; i<reqTempControlsSize; i++){
                reqRelayControl=reqRelayControls.get(i);
                byte[] reqData= ReqRelayControl.valueOf(reqRelayControl);

                //超时响应时间：10S。
                Command cmd = Command.create(CommandDef.ID_ACCS_CONTORL,reqData, Command.TYPE_SYNC);
                machine.executeCommand(cmd);

                if (cmd.isError()){
                    printCmdErrorInfo(mComId,cmd);
                }
                {
                    if(null!=cmd.getResult()) {
                        byte[] rspData = cmd.getResult().getData();
                        if (null == rspData) {
                            Logger.sLogger4Machine.info(TAG, "createCommand4RelayControl cmd.getResult().getData()==null");
                            break;
                        }
                        rspRelayControl = RspRelayControl.valueOf(rspData);
                        if (null != rspRelayControl) {
                            rspRelayControls.add(rspRelayControl);
                        } else {
                            Logger.sLogger4Machine.info(TAG, "null == rspTempControl[" + Tools.bytesToHexString(rspData) + "]无法解析");
                        }
                    }else {
                        Logger.sLogger4Machine.info(TAG, "createCommand4RelayControl cmd.getResult()==null");
                    }
                }
            }
        }while (false);

        return rspRelayControls;
    }






    /**
     * 查询传感器状态(同步)
     * @return
     */
    public RspGetSensorState createCommand4GetSensorStateBySync(){
        RspGetSensorState rspGetSensorState=null;
        do{


            Machine machine = findMachine();
            if (null == machine) {
                Logger.sLogger4Machine.info(TAG, "createCommand4GetSensorStateBySync null == machine mComId=" + mComId);
                break;
            }

            Command cmd = Command.create(CommandDef.ID_GET_SENSORA_STATE, Command.TYPE_SYNC);
            machine.executeCommand(cmd);

            if (cmd.isError()){
                printCmdErrorInfo(mComId,cmd);
            }

            {
                if(null!=cmd.getResult()) {
                    byte[] data = cmd.getResult().getData();
                    if (null == data) {
                        Logger.sLogger4Machine.info(TAG, "createCommand4GetSensorStateBySync cmd.getResult().getData()==null");
                        break;
                    }

                    rspGetSensorState = RspGetSensorState.valueOf(data);
                    if (null == rspGetSensorState) {
                        Logger.sLogger4Machine.info(TAG, "null == rspGetSensorState[" + Tools.bytesToHexString(data) + "]无法解析");
                        break;
                    }
                }else {
                    Logger.sLogger4Machine.info(TAG, "rspGetSensorState cmd.getResult()==null");
                }
            }

        }while (false);

        return rspGetSensorState;
    }
    /**
     * 读写取货高度补偿值请求(同步)
     * @param reqRwPickOffset
     */
    public RspRwPickOffset createCommand4ReqRwPickOffsetBySync(ReqRwPickOffset reqRwPickOffset){
        RspRwPickOffset rspRwPickOffset=null;
        do{

            if(null==reqRwPickOffset){
                Logger.sLogger4Machine.info(TAG,"null==reqRwPickOffset");
                break;
            }

            Machine machine = findMachine();
            if (null == machine) {
                Logger.sLogger4Machine.info(TAG, "createCommand4ReqRwPickOffsetBySync null == machine mComId=" + mComId);
                break;
            }

            byte[] reqData=ReqRwPickOffset.valueOf(reqRwPickOffset);
            Command cmd = Command.create(CommandDef.ID_RW_PICK_OFFSET,reqData,Command.TYPE_SYNC);
            machine.executeCommand(cmd);

            if (cmd.isError()){
                printCmdErrorInfo(mComId,cmd);
            }

            {

                if(null!=cmd.getResult()) {
                    byte[] data = cmd.getResult().getData();
                    if (null == data) {
                        Logger.sLogger4Machine.info(TAG, "createCommand4ReqRwPickOffsetBySync cmd.getResult().getData()==null");
                        break;
                    }
                    rspRwPickOffset = RspRwPickOffset.valueOf(data);
                    if (null != rspRwPickOffset) {

                    } else {
                        Logger.sLogger4Machine.info(TAG, "null == rspRwPickOffset[" + Tools.bytesToHexString(data) + "]无法解析");
                    }
                }else {
                    Logger.sLogger4Machine.info(TAG, "rspRwPickOffset cmd.getResult()==null");
                }
            }

        }while (false);

        return rspRwPickOffset;
    }


    /**
     * 读写出货口高度请求(同步)
     * @param reqRwPopVal
     */
    public RspRwPopVal createCommand4ReqRwPopValBySync(ReqRwPopVal reqRwPopVal){
        RspRwPopVal rspRwPopVal=null;
        do{

            if(null==reqRwPopVal){
                Logger.sLogger4Machine.info(TAG,"null==reqRwPopVal");
                break;
            }

            Machine machine = findMachine();
            if (null == machine) {
                Logger.sLogger4Machine.info(TAG, "createCommand4ReqRwPickOffsetBySync null == machine mComId=" + mComId);
                break;
            }

            byte[] reqData= ReqRwPopVal.valueOf(reqRwPopVal);
            Command cmd = Command.create(CommandDef.ID_RW_POP_VAL,reqData,Command.TYPE_SYNC);
            machine.executeCommand(cmd);

            if (cmd.isError()){
                printCmdErrorInfo(mComId,cmd);
            }

            {

                if(null!=cmd.getResult()) {
                    byte[] data = cmd.getResult().getData();
                    if (null == data) {
                        Logger.sLogger4Machine.info(TAG, "createCommand4ReqRwPickOffsetBySync cmd.getResult().getData()==null");
                        break;
                    }
                    rspRwPopVal = RspRwPopVal.valueOf(data);
                    if (null != rspRwPopVal) {

                    } else {
                        Logger.sLogger4Machine.info(TAG, "null == rspRwPopVal[" + Tools.bytesToHexString(data) + "]无法解析");
                    }
                }else {
                    Logger.sLogger4Machine.info(TAG, "rspRwPopVal cmd.getResult()==null");
                }
            }


        }while (false);

        return rspRwPopVal;
    }





    /**
     * start: TEST
     */
    public RspRwSystemParameter createCommand4ReqRwSystemParameterBySync(byte slaverId,byte cmd,byte para,ReqRwSystemParameter reqRwSystemParameter){
        RspRwSystemParameter rspRwSystemParameter=null;
        do{

            if(null==reqRwSystemParameter){
                Logger.sLogger4Machine.info(TAG,"null==reqRwSystemParameter");
                break;
            }
            Machine machine = findMachine();
            if (null == machine) {
                Logger.sLogger4Machine.info(TAG, "createCommand4ReqRwSystemParameterBySync null == machine mComId=" + mComId);
                break;
            }

            IpsMsg reqIpsMcsMsg= IpsMsgUtil.valueOf(slaverId,cmd,para,reqRwSystemParameter.getBytes());

            ReqUnitTest reqUnitTest=new ReqUnitTest(reqIpsMcsMsg.getMsg());
            if(null==reqUnitTest.getBytes()){
                Logger.sLogger4Machine.info(TAG,"null==reqUnitTest.getBytes()");
                break;
            }

            Logger.sLogger4Machine.info(TAG,"ReqUnitTest data["+Tools.bytesToHexString(reqUnitTest.getBytes())+"]");
            Logger.sLogger4Machine.info(TAG,"reqIpsMcsMsg.getMsg()["+Tools.bytesToHexString(reqIpsMcsMsg.getMsg())+"]");
            Logger.sLogger4Machine.info(TAG,"reqIpsMcsMsg.getData()["+Tools.bytesToHexString(reqIpsMcsMsg.getData())+"]");

            Command command = Command.create(CommandDef.ID_UNIT_TEST,reqUnitTest.getBytes(),Command.TYPE_SYNC);
            machine.executeCommand(command);

            if (command.isError()){
                printCmdErrorInfo(mComId,command);
            }

            {
                if(null!=command.getResult()) {
                    byte[] data = command.getResult().getData();
                    if (null == data) {
                        Logger.sLogger4Machine.info(TAG, "createCommand4ReqRwPickOffsetBySync cmd.getResult().getData()==null");
                        break;
                    }

                    RspUnitTest rspUnitTest = RspUnitTest.valueOf(data);
                    if (null == rspUnitTest) {
                        Logger.sLogger4Machine.info(TAG, "null==rspUnitTest");
                        break;
                    }

                    if (null == rspUnitTest.getModuleRspPacket()) {
                        Logger.sLogger4Machine.info(TAG, "null==rspUnitTest.getModuleRspPacket()");
                        break;
                    }

                    IpsMsg rspIpsMcsMsg = IpsMsgUtil.valueOf(rspUnitTest.getModuleRspPacket());

                    if (null == reqIpsMcsMsg.getData()) {
                        Logger.sLogger4Machine.info(TAG, "null==reqIpsMcsMsg.getData()");
                        break;
                    }

                    Logger.sLogger4Machine.info(TAG, "RspUnitTest data[" + Tools.bytesToHexString(data) + "]");
                    Logger.sLogger4Machine.info(TAG, "RspUnitTest rspUnitTest.getModuleRspPacket()[" + Tools.bytesToHexString(rspUnitTest.getModuleRspPacket()) + "]");
                    Logger.sLogger4Machine.info(TAG, "reqIpsMcsMsg.getData()[" + Tools.bytesToHexString(reqIpsMcsMsg.getData()) + "]");

                    rspRwSystemParameter = RspRwSystemParameter.valueOf(rspIpsMcsMsg.getData());
                    if (null != rspRwSystemParameter) {

                    } else {
                        Logger.sLogger4Machine.info(TAG, "null == rspRwSystemParameter[" + Tools.bytesToHexString(rspIpsMcsMsg.getData()) + "]无法解析");
                    }
                }else {
                    Logger.sLogger4Machine.info(TAG, "rspRwSystemParameter cmd.getResult()==null");
                }
            }


        }while (false);

        return rspRwSystemParameter;
    }
    /**
     * end: TEST
     */



    private void initMachine(){
        if(null!=mSerialPortInfo&& (false== CommonUtil.isEmpty(mSerialPortInfo.getSerialPortDeviceName()))) {
            Logger.sLogger4Machine.info(TAG, "动态适配主板:"+JsonUtil.toJson(mSerialPortInfo)+"DeviceNo["+mDeviceNo+"]"+"android.os.Build.MODEL: ["+android.os.Build.MODEL+"]");
        }else {
            mSerialPortInfo = new SerialPortInfo(AndroidBoardConstant.BANGMART.SerialPortInfoConstant.SerialPortTwo.COM_ID, BmtMsgConstant.BAUD_RATE, AndroidBoardConstant.BANGMART.SerialPortInfoConstant.SerialPortTwo.SERIAL_PORT_DEVICE_NAME);
            Logger.sLogger4Machine.info(TAG, "默认主板:"+JsonUtil.toJson(mSerialPortInfo)+"DeviceNo["+mDeviceNo+"]"+"android.os.Build.MODEL: ["+android.os.Build.MODEL+"]");
        }

        mComId=mSerialPortInfo.getComId();
        mMachine = Machine.create(getMachineID(mDeviceNo), mSerialPortInfo.getSerialPortDeviceName());
    }

    /**
     * 获取机器标识(10位)
     *
     * @param deviceNo (10位)
     * @return
     */
    public static String getMachineID(String deviceNo) {
        String machineId = DEFAULT_DEVICE_NO;
        do {

            if (TextUtils.isEmpty(deviceNo)) {
                break;
            }

            if (deviceNo.length() != DEFAULT_DEVICE_NO_LENGTH) {
                break;
            }

            machineId = deviceNo ;
        } while (false);


        return machineId;
    }








    private Machine findMachine() {
        return mMachine;
    }


    /**
     *
     * @param cmd
     */
    private void printCmdErrorInfo(int mComId,Command cmd){
        do{
            if(null==cmd){
                break;
            }

            if(false==cmd.isError()){
                break;
            }

            String tips=null;
            if(null!=cmd.getResult()) {
                if(null!=cmd.getResult().getCode()) {
                    tips = cmd.getErrorDescription() + " : " + Tools.bytesToHexString(cmd.getResult().getCode());
                }else {
                    tips = cmd.getErrorDescription();
                }
            }else {
                tips = cmd.getErrorDescription();
            }

            Logger.sLogger4Machine.info(TAG, "mComId=[" + mComId + "]" + tips);
        }while (false);
    }




}
