package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;
import java.util.List;

public class MBangmartAuthPickUpRequest implements Serializable {

    /**
     * 卡号
     */
    private String CardNo;

    public String getCardNo() {
        return CardNo;
    }

    public void setCardNo(String cardNo) {
        CardNo = cardNo;
    }

    public String getMachineCode() {
        return MachineCode;
    }

    public void setMachineCode(String machineCode) {
        MachineCode = machineCode;
    }

    public List<MBangmartProductDetail> getDetail() {
        return Detail;
    }

    public void setDetail(List<MBangmartProductDetail> detail) {
        Detail = detail;
    }

    /**
     * 设备编号
     */
    private String MachineCode;

    /**
     * 领取详情
     */
    private List<MBangmartProductDetail> Detail;

}
