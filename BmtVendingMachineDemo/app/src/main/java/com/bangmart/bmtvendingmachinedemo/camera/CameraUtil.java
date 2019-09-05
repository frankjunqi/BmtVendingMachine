package com.bangmart.bmtvendingmachinedemo.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;

import com.bangmart.bmtvendingmachinedemo.androidboard.constant.AndroidBoardModel;
import com.bangmart.bmtvendingmachinedemo.androidboard.util.AndroidBoardModelUtil;
import com.bangmart.bmtvendingmachinedemo.util.CommandUtil;

import java.util.List;


/**
 * Created by zhoujq on 2018/3/27.
 */

public class CameraUtil {
    public static final String TAG="CameraUtil";
    //检测摄像头是否挂载命令
    public static final String CKECK_CAMERA_COMMAND_STR = "cat /sys/kernel/debug/usb/devices"+"\n";

    /**
     * 检测是否有摄像头
     * @param context
     * @return
     */
    public static boolean checkCameraAvaliable(Context context){
        boolean cameraAvailible  = false;

        if(AndroidBoardModelUtil.getAndroidBoardModel() == AndroidBoardModel.SMDT_ANDROID_BOARD_MODEL){
            //视美泰主板(维冠一体机使用的主板),检测方式不用系统api检测的方式
            cameraAvailible = checkSmdtCameraAvaliable();
        }else {
            cameraAvailible = generalCheckcheckCameraAvaliable(context);
        }

        if(!cameraAvailible){
            System.out.println("没有检测到摄像头");

        }

        return cameraAvailible;
    }

    /**
     * 视美泰主板(维冠一体机使用的主板)检测方式
     */
    private static boolean checkSmdtCameraAvaliable() {
        boolean cameraAvailible = false;
        List<String> executeRes    = CommandUtil.execute(CKECK_CAMERA_COMMAND_STR);
        if(executeRes==null || executeRes.size()<=0){
            return false;
        }

        for(String resLine : executeRes) {
            if(resLine.contains("Vendor="+CameraDevInfo.AlipayD2Info.VID) && resLine.contains("ProdID="+CameraDevInfo.AlipayD2Info.PID)){
                cameraAvailible = true;
                break;
            }

            if(resLine.contains("Vendor="+CameraDevInfo.AlipayProAInfo.VID)&& resLine.contains("ProdID="+CameraDevInfo.AlipayProAInfo.PID)){
                cameraAvailible = true;
                break;
            }
        }

        return cameraAvailible;
    }

    /**
     * 通用的通过系统api的方式来检测摄像头是否挂载的方法
     * @param context
     * @return
     */
    private static boolean generalCheckcheckCameraAvaliable(Context context){
        boolean cameraAvailible = false;
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            cameraAvailible =  false;
        }

        //5.0及以上的系统版本
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            CameraManager manager   = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String[]      cameraIds = new String[0];
            try {
                cameraIds = manager.getCameraIdList();
                if (cameraIds != null && cameraIds.length > 0) {
                    cameraAvailible = true;
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
                cameraAvailible = false;
            }
        }else {
            //5.0以下的系统
            final int cameraCount = Camera.getNumberOfCameras();
            if(cameraCount>0){
                cameraAvailible = true;
            }else {
                cameraAvailible = false;
            }
        }

        return cameraAvailible;
    }
}
