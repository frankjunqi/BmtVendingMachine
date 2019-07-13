package com.seekwork.bangmart.network.api;


import com.seekwork.bangmart.network.entity.seekwork.MBangmarPickRoadDetailRequest;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarPickRoadDetailResponse;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarPickSuccessRequest;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartAuthPickUpRequest;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartAuthPickUpResponse;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartDetail;
import com.seekwork.bangmart.network.entity.seekwork.MDoDisableRoodRequest;
import com.seekwork.bangmart.network.entity.seekwork.MMachineInfo;
import com.seekwork.bangmart.network.entity.seekwork.MPickQueryByRFID;
import com.seekwork.bangmart.network.entity.seekwork.MRecoverRoadStateRequest;
import com.seekwork.bangmart.network.entity.seekwork.MReplenish;
import com.seekwork.bangmart.network.entity.seekwork.MReplenishToRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 *
 */
public interface SeekWorkService {

    /**
     * 1. 启动APP 首先生产设备唯一码
     *
     * @param vendingMachineGuid 设备唯一码
     * @return
     */
    @GET("/BangMart/GetMachineInfo")
    Call<SrvResult<MMachineInfo>> getMachineInfo(
            @Query("vendingMachineGuid") String vendingMachineGuid
    );


    /**
     * 2. 请求服务，验证是否是管理员卡
     *
     * @param machineCode 设备编号
     * @param cardNo      卡号
     * @return
     */
    @GET("ManageOperate/LoginValidate")
    Call<SrvResult<Boolean>> loginValidate(
            @Query("machineCode") String machineCode,
            @Query("cardNo") String cardNo
    );


    /**
     * 3. 获取所有货道详情
     *
     * @param machineCode 设备编号
     * @return
     */
    @GET("BangMart/QueryRoad")
    Call<SrvResult<MBangmartDetail>> queryRoad(
            @Query("machineCode") String machineCode
    );


    /**
     * 4. 购物车授权验证
     *
     * @param mBangmartAuthPickUpRequest 详细数据
     * @return
     */
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @POST("BangMart/PickQuery")
    Call<SrvResult<MBangmartAuthPickUpResponse>> PickQuery(
            @Body MBangmartAuthPickUpRequest mBangmartAuthPickUpRequest);


    /**
     * 5. 获取购物货道
     *
     * @param mBangmarPickRoadDetailRequest
     * @return
     */
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @POST("BangMart/GetPickUpRoads")
    Call<SrvResult<MBangmarPickRoadDetailResponse>> GetPickUpRoads(
            @Body MBangmarPickRoadDetailRequest mBangmarPickRoadDetailRequest);


    /**
     * 6. 成功购物
     *
     * @param mBangmarPickSuccessRequest
     * @return
     */
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @POST("BangMart/DoPickSuccess")
    Call<SrvResult<Boolean>> DoPickSuccess(
            @Body MBangmarPickSuccessRequest mBangmarPickSuccessRequest);


    /**
     * 7. 禁用货道
     *
     * @param mDoDisableRoodRequest
     * @return
     */
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @POST("ManageOperate/DoDisableRoads")
    Call<SrvResult<Boolean>> DoDisableRoads(
            @Body MDoDisableRoodRequest mDoDisableRoodRequest);


    /**
     * 8. 恢复货道
     *
     * @param mRecoverRoadStateRequest
     * @return
     */
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @POST("ManageOperate/RecoverRoadState")
    Call<SrvResult<Boolean>> RecoverRoadState(
            @Body MRecoverRoadStateRequest mRecoverRoadStateRequest);

    /**
     * 9. 货道补货
     *
     * @param mReplenishToRequest
     * @return
     */
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @POST("ManageOperate/ReplenishTo")
    Call<SrvResult<Boolean>> ReplenishTo(@Body MReplenishToRequest mReplenishToRequest);


    // 刷卡取货
    @GET("PickUp/PickQueryByRFID")
    Call<SrvResult<MPickQueryByRFID>> pickQueryByRFID(
            @Query("cardNo") String cardNo,
            @Query("machineCode") String machineCode,
            @Query("cabNo") String cabNo,
            @Query("roadCode") String roadCode,
            @Query("pickNum") int pickNum
    );


    // 刷卡取货成功提交确认
    @GET("PickUp/PickSuccess")
    Call<SrvResult<Boolean>> pickSuccess(
            @Query("cardNo") String cardNo,
            @Query("machineCode") String machineCode,
            @Query("cabNo") String cabNo,
            @Query("roadCode") String roadCode,
            @Query("pickNum") int pickNum
    );

    // 此货道被谁借走
    @GET("BorrowAndBack/GetLastBorrowName")
    Call<SrvResult<String>> getLastBorrowName(
            @Query("machineCode") String machineCode,
            @Query("cabNo") String cabNo,
            @Query("roadCode") String roadCode
    );


    // 借货 还货 请求
    @GET("BorrowAndBack/AuthBorrowAndBack")
    Call<SrvResult<Boolean>> authBorrowAndBack(
            @Query("cardNo") String cardNo,
            @Query("machineCode") String machineCode,
            @Query("cabNo") String cabNo,
            @Query("roadCode") String roadCode,
            @Query("BorrowBackType") int BorrowBackType
    );

    // 借货成功确认
    @GET("BorrowAndBack/BorrowComplete")
    Call<SrvResult<Boolean>> borrowComplete(
            @Query("cardNo") String cardNo,
            @Query("machineCode") String machineCode,
            @Query("cabNo") String cabNo,
            @Query("roadCode") String roadCode,
            @Query("successCount") int successCount
    );

    // 还货成功确认
    @GET("BorrowAndBack/BackComplete")
    Call<SrvResult<Boolean>> backComplete(
            @Query("cardNo") String cardNo,
            @Query("machineCode") String machineCode,
            @Query("cabNo") String cabNo,
            @Query("roadCode") String roadCode,
            @Query("successCount") int successCount
    );
}
