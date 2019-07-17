package com.seekwork.bangmart.network.api;


import com.seekwork.bangmart.network.entity.seekwork.MBangmarPickRoadDetailRequest;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarPickRoadDetailResponse;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarPickSuccessRequest;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartAuthPickUpRequest;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartAuthPickUpResponse;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartDetail;
import com.seekwork.bangmart.network.entity.seekwork.MDoDisableRoodRequest;
import com.seekwork.bangmart.network.entity.seekwork.MMachineInfo;
import com.seekwork.bangmart.network.entity.seekwork.MRecoverRoadStateRequest;
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
    @GET("/api/BangMart/GetMachineInfo")
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
    @GET("/api/ManageOperate/LoginValidate")
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
    @GET("/api/BangMart/QueryRoad")
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
    @POST("/api/BangMart/PickQuery")
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
    @POST("/api/BangMart/GetPickUpRoads")
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
    @POST("/api/BangMart/DoPickSuccess")
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
    @POST("/api/ManageOperate/DoDisableRoads")
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
    @POST("/api/ManageOperate/RecoverRoadState")
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
    @POST("/api/ManageOperate/ReplenishTo")
    Call<SrvResult<Boolean>> ReplenishTo(@Body MReplenishToRequest mReplenishToRequest);

}
