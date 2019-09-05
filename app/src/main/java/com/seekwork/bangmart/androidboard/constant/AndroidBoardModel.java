package com.seekwork.bangmart.androidboard.constant;

/**
 * Created by zhoujq on 2019/1/22.
 * android板型号
 */
public interface AndroidBoardModel {
    /**
     * 邦马特android板型号
     */
    int BANGMART_ANDROID_BOARD_MODEL=1;
    /**
     * 富士康android板型号
     */
    int FSK_COMMON_ANDROID_BOARD_MODEL=2;

    /**
     * 彤兴Android板
     * {@link AndroidBoardConstant.OSTAR}
     */
    int OSTAR_ANDROID_BOARD_MODEL =3;

    /**
     * 众云世纪 //视美讯RK3288
     * {@link AndroidBoardConstant.ZHONG_YUN_SHI_JI}
     */
    int ZHONG_YUN_SHI_JI_ANDROID_BOARD_MODEL =4;

    /**
     * 中吉无屏幕(注意机器号不能清空)
     *{@link AndroidBoardModelConstant#ZHONGJI_NOLIFTER_NO_SCREEN_ANDROID_BOARD_MODEL}
     */
    int ZHONGJI_NOLIFTER_NO_SCREEN_ANDROID_BOARD_MODEL =5;

    /**
     * 其它待添加
     */

    /**
     * 云印-昱显android板
     * {@link AndroidBoardModelConstant#YUN_YIN_AND_YU_XIAN_ANDROID_BOARD_MODEL}
     * {@link AndroidBoardConstant.YUN_YIN_AND_YU_XIAN}
     */
    int YUN_YIN_AND_YU_XIAN_ANDROID_BOARD_MODEL=6;

    /**
     * 视美泰3280型号主板(维冠一体机使用的主板,主要3代机使用)
     *{@link AndroidBoardModelConstant#SMDT_ANDROID_BOARD_MODEL}
     * {@link AndroidBoardConstant.SHI_MEI_TAI_SMDT}
     */
    int SMDT_ANDROID_BOARD_MODEL =7;


    /**
     * 未知
     */
    int UNKNOWN=0;
}
