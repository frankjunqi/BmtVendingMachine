package com.seekwork.bangmart.androidboard.util;


import com.bangmart.nt.common.constant.Constant;
import com.bangmart.nt.common.util.CommonUtil;
import com.seekwork.bangmart.androidboard.constant.AndroidBoardConstant;
import com.seekwork.bangmart.androidboard.constant.AndroidBoardModel;
import com.seekwork.bangmart.androidboard.constant.AndroidBoardModelConstant;
import com.seekwork.bangmart.androidboard.constant.ComUse;
import com.seekwork.bangmart.model.SerialPortInfo;

/**
 * Created by zhoujq on 2019/1/22.
 */
public class AndroidBoardModelUtil {


    /**
     * @return
     */
    public static SerialPortInfo getSerialPortInfo(int comUse) {
        SerialPortInfo serialPortInfo = null;
        do {
  /*          if (true == CommonUtil.isEmpty(deviceNo)) {
                break;
            }*/

            String serialPortDeviceName = Constant.NULL_STRING;
            int comId = 0;
            switch (comUse) {
                case ComUse.VENDING_MACHINE: {
                    int androidBoardModel = getAndroidBoardModel();
                    switch (androidBoardModel) {
                        case AndroidBoardModel.BANGMART_ANDROID_BOARD_MODEL:
                            serialPortDeviceName = AndroidBoardConstant.BANGMART.SerialPortInfoConstant.SerialPortTwo.SERIAL_PORT_DEVICE_NAME;
                            comId = AndroidBoardConstant.BANGMART.SerialPortInfoConstant.SerialPortTwo.COM_ID;
                            break;
                        case AndroidBoardModel.FSK_COMMON_ANDROID_BOARD_MODEL:
                            serialPortDeviceName = AndroidBoardConstant.FSK.SerialPortInfoConstant.SerialPortOne.SERIAL_PORT_DEVICE_NAME;
                            comId = AndroidBoardConstant.FSK.SerialPortInfoConstant.SerialPortOne.COM_ID;
                            break;
                        case AndroidBoardModel.OSTAR_ANDROID_BOARD_MODEL:
                            serialPortDeviceName = AndroidBoardConstant.OSTAR.SerialPortInfoConstant.SerialPortOne.SERIAL_PORT_DEVICE_NAME;
                            comId = AndroidBoardConstant.OSTAR.SerialPortInfoConstant.SerialPortOne.COM_ID;
                            break;
                        case AndroidBoardModel.ZHONG_YUN_SHI_JI_ANDROID_BOARD_MODEL:
                            serialPortDeviceName = AndroidBoardConstant.ZHONG_YUN_SHI_JI.SerialPortInfoConstant.SerialPortTwo.SERIAL_PORT_DEVICE_NAME;
                            comId = AndroidBoardConstant.ZHONG_YUN_SHI_JI.SerialPortInfoConstant.SerialPortTwo.COM_ID;
                            break;
                        case AndroidBoardModel.YUN_YIN_AND_YU_XIAN_ANDROID_BOARD_MODEL:
                            serialPortDeviceName = AndroidBoardConstant.YUN_YIN_AND_YU_XIAN.SerialPortInfoConstant.SerialPortFour.SERIAL_PORT_DEVICE_NAME;
                            comId = AndroidBoardConstant.YUN_YIN_AND_YU_XIAN.SerialPortInfoConstant.SerialPortFour.COM_ID;
                            break;

                        case AndroidBoardModel.SMDT_ANDROID_BOARD_MODEL:
                            serialPortDeviceName = AndroidBoardConstant.SHI_MEI_TAI_SMDT.SerialPortInfoConstant.SerialPortThree.SERIAL_PORT_DEVICE_NAME;
                            comId = AndroidBoardConstant.SHI_MEI_TAI_SMDT.SerialPortInfoConstant.SerialPortThree.COM_ID;
                            break;
                        default:
                            break;
                    }
                }
                break;

                case ComUse.PRINTER: {
                    int androidBoardModel = getAndroidBoardModel();
                    switch (androidBoardModel) {
                        case AndroidBoardModel.BANGMART_ANDROID_BOARD_MODEL:
                            serialPortDeviceName = AndroidBoardConstant.BANGMART.SerialPortInfoConstant.SerialPortThree.SERIAL_PORT_DEVICE_NAME;
                            comId = AndroidBoardConstant.BANGMART.SerialPortInfoConstant.SerialPortThree.COM_ID;
                            break;
                        case AndroidBoardModel.FSK_COMMON_ANDROID_BOARD_MODEL:
                            serialPortDeviceName = AndroidBoardConstant.FSK.SerialPortInfoConstant.SerialPortTwo.SERIAL_PORT_DEVICE_NAME;
                            comId = AndroidBoardConstant.FSK.SerialPortInfoConstant.SerialPortTwo.COM_ID;
                            break;
                        case AndroidBoardModel.OSTAR_ANDROID_BOARD_MODEL:
                            //暂未支持
                            break;
                        case AndroidBoardModel.ZHONG_YUN_SHI_JI_ANDROID_BOARD_MODEL:
                            serialPortDeviceName = AndroidBoardConstant.ZHONG_YUN_SHI_JI.SerialPortInfoConstant.SerialPortThree.SERIAL_PORT_DEVICE_NAME;
                            comId = AndroidBoardConstant.ZHONG_YUN_SHI_JI.SerialPortInfoConstant.SerialPortThree.COM_ID;
                            break;
                        case AndroidBoardModel.YUN_YIN_AND_YU_XIAN_ANDROID_BOARD_MODEL:
                            //暂未支持
                            break;
                        case AndroidBoardModel.SMDT_ANDROID_BOARD_MODEL:
                            serialPortDeviceName = AndroidBoardConstant.SHI_MEI_TAI_SMDT.SerialPortInfoConstant.SerialPortOne.SERIAL_PORT_DEVICE_NAME;
                            comId = AndroidBoardConstant.SHI_MEI_TAI_SMDT.SerialPortInfoConstant.SerialPortOne.COM_ID;
                            break;
                        default:
                            break;
                    }
                }
                break;
                default:
                    break;
            }

            //int baudRate = AndroidBoardModelUtil.getBaudRate(deviceNo);


            if (false == CommonUtil.isEmpty(serialPortDeviceName)) {
                serialPortInfo = new SerialPortInfo(comId, 0, serialPortDeviceName);
            }

        } while (false);

        return serialPortInfo;
    }

    /**
     * 获取android板model
     *
     * @return
     */
    public static int getAndroidBoardModel() {
        int androidBoardModel = AndroidBoardModel.UNKNOWN;

        do {
            String model = android.os.Build.MODEL;
            if (true == CommonUtil.isEmpty(model)) {
                break;
            }

            if (model.equals(AndroidBoardModelConstant.BANGMART_ANDROID_BOARD_MODEL) || model.equals(AndroidBoardModelConstant.BANGMART_ANDROID_BOARD_MODEL2)) {
                androidBoardModel = AndroidBoardModel.BANGMART_ANDROID_BOARD_MODEL;
                break;
            }

            if (model.equals(AndroidBoardModelConstant.FSK_COMMON_ANDROID_BOARD_MODEL)) {
                androidBoardModel = AndroidBoardModel.FSK_COMMON_ANDROID_BOARD_MODEL;
                break;
            }

            if (model.equals(AndroidBoardModelConstant.OSTAR_ANDROID_BOARD_MODEL)) {
                androidBoardModel = AndroidBoardModel.OSTAR_ANDROID_BOARD_MODEL;
                break;
            }

            if (model.equals(AndroidBoardModelConstant.ZHONG_YUN_SHI_JI_ANDROID_BOARD_MODEL)) {
                androidBoardModel = AndroidBoardModel.ZHONG_YUN_SHI_JI_ANDROID_BOARD_MODEL;
                break;
            }

            if (model.equals(AndroidBoardModelConstant.ZHONGJI_NOLIFTER_NO_SCREEN_ANDROID_BOARD_MODEL)) {
                androidBoardModel = AndroidBoardModel.ZHONGJI_NOLIFTER_NO_SCREEN_ANDROID_BOARD_MODEL;
                break;
            }

            if (model.equals(AndroidBoardModelConstant.YUN_YIN_AND_YU_XIAN_ANDROID_BOARD_MODEL)) {
                androidBoardModel = AndroidBoardModel.YUN_YIN_AND_YU_XIAN_ANDROID_BOARD_MODEL;
                break;
            }

            if (model.equals(AndroidBoardModelConstant.SMDT_ANDROID_BOARD_MODEL)) {
                androidBoardModel = AndroidBoardModel.SMDT_ANDROID_BOARD_MODEL;
                break;
            }
        } while (false);

        return androidBoardModel;
    }
}
