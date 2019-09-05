package com.bangmart.bmtvendingmachinedemo.camera;

/*
 *  @项目名：  VendingMachine
 *  @包名：    com.gzyx.vendingmachine.camera
 *  @文件名:   CameraDevInfo
 *  @创建者:   Administrator
 *  @描述：    摄像头相关常量
 *
 *  说明:pid&vid 可以唯一识别一种usb设备,本质上 这个值是一个16进制的数字
 */
public class CameraDevInfo {

    /**
     *支付宝刷脸摄像头D2型号
     */
    interface AlipayD2Info{
        String VID = "2bc5";
        String PID = "060d";
    }

    /**
     * 支付宝刷脸摄像头ProA型号
     */
    //TODO:
    interface AlipayProAInfo{
        String VID = "2bc5";
        String PID = "060d";
    }
}
