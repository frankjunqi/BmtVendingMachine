package com.bangmart.bmtvendingmachinedemo.androidboard.constant;

/**
 * Created by zhoujq on 2019/1/22.
 * Android型号常量 android.os.Build.MODEL
 */
public interface AndroidBoardModelConstant {

    /**
     * 邦马特Android板型号
     */
    String BANGMART_ANDROID_BOARD_MODEL="rk3288_box";
    String BANGMART_ANDROID_BOARD_MODEL2="EMB3550_V1.1-KK-SZZYB001";

    /**
     *富士康Android板型号
     */
    String FSK_COMMON_ANDROID_BOARD_MODEL="SABRESD-MX6DQ";

    /**
     * 彤兴Android板
     * {@link AndroidBoardConstant.OSTAR}
     */
    String OSTAR_ANDROID_BOARD_MODEL ="A2251G23";

    /**
     * 众云世纪 //视美讯RK3288
     * {@link AndroidBoardConstant.ZHONG_YUN_SHI_JI}
     */
    String ZHONG_YUN_SHI_JI_ANDROID_BOARD_MODEL ="rk3288";

    /**
     * 中吉无屏幕(注意机器号不能清空)
     */
    String ZHONGJI_NOLIFTER_NO_SCREEN_ANDROID_BOARD_MODEL ="BMT-YQJ";

    /**
     * 云印 5.0 金顺android板 //待确认
     */
   // String YUN_YIN_AND_JIN_SHUN_ANDROID_BOARD_MODEL ="m68";

    /**
     *云印-昱显android板
     *
     * MODEL: yx_rk3288
     * 串口：/dev/ttyS4
     */
    String YUN_YIN_AND_YU_XIAN_ANDROID_BOARD_MODEL="yx_rk3288";

    /**
     * 视美泰3280型号主板(维冠一体机使用的主板,主要3代机使用)
     */
    String SMDT_ANDROID_BOARD_MODEL ="3280";


    /**
     * 云印 6.0 闪卓android板
     */
    //String YUN_YIN_AND_SHAN_ZHOU_ANDROID_BOARD_MODEL ="";

}
