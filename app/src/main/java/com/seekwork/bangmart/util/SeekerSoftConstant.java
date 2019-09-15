package com.seekwork.bangmart.util;

import com.seekwork.bangmart.model.SerialLocationBean;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartRoad;

import java.util.HashMap;
import java.util.List;

/**
 *
 */

public class SeekerSoftConstant {

    public final static String MACHINE = "BangMartCo";
    public final static long replyTimeout = 1000;
    public final static long responseTimeout = 3000;

    // 柜子的数据：包括 area follor column x y width
    public static List<List<List<SerialLocationBean>>> list;
    public static HashMap<String, List<MBangmartRoad>> hashMap = new HashMap<>();
    ;

    // 小车的宽度
    public static final int MACHINE_WIDTH = 300;
    // 小车两个柜子的间距
    public static final int MACHINE_BETWEEN = 130;
    // 小车托盘的宽度
    public final static int MACHINE_FACT_WIDTH = 280;
    // 小车内真实有效内部的宽度
    public final static int MACHINE_IN_FACT_WIDTH = 260;
    // 小车的真实的高度
    public final static int MACHINT_IN_FACT_HEIGHT = 210;

    // 右极限的数值宽度
    public static int RIGHT_LIMIT = 0;
    // 顶部极限数字高度
    public static int TOP_LIMIT = 0;
    // 柜子的区域数目
    public static int AREA_COUNT = 0;
    // 货柜的整体宽度
    public static int BOX_WIDTH = 0;
    // 货柜的整体高度
    public static int BOX_HEIGHT = 0;

    // 初始化系统的时间
    public static int INIT_SYS_TIME = 18;
    // 商品列表页面到技术倒计时
    public static int INIT_PRO_LIST_TIME = 100;
    // 购物车页面的倒计时
    public static int SHOPCART_LIST_TIME = 30;


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
    public static final String INTENT_INT_INDEX = "intent_int_index";
    public static final String CHOOSE_POSITION = "CHOOSE_POSITION";
}
