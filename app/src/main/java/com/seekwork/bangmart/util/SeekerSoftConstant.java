package com.seekwork.bangmart.util;

import com.bangmart.nt.machine.Machine;
import com.bangmart.nt.machine.StorageMap;

/**
 *
 */

public class SeekerSoftConstant {

    public final static String MACHINE = "BangMartCo";
    public final static long replyTimeout = 1000;
    public final static long responseTimeout = 300000;

    // 小车的宽度
    public final static int MACHINE_WIDTH = 280;


    public static Machine machine;
    public static StorageMap storageMap;

    // 正式包变成false
    public static final boolean DEBUG = true;
    // 设备号
    public static String DEVICEID = "XXXXXXXXXXX";
    // 网络是否链接
    public static boolean NETWORKCONNECT = true;
    public static String MachineNo = "";

    public static final String EXITAPP = "EXITAPP";

    public static final String ADDCARTLIST = "addcartlist";


    public static final String OUTCART = "OUTCART";
    public static final String CardNo = "CardNo";
    public static final String OrderID = "OrderID";


}
