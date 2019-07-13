package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;
import java.util.List;

public class MBangmartDetail implements Serializable {

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public List<MBangmartArea> getmBangmartAreas() {
        return mBangmartAreas;
    }

    public void setmBangmartAreas(List<MBangmartArea> mBangmartAreas) {
        this.mBangmartAreas = mBangmartAreas;
    }

    private String machineCode;

    private List<MBangmartArea> mBangmartAreas;

}
