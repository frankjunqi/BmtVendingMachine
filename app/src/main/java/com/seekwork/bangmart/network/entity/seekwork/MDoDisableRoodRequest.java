package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;
import java.util.List;

public class MDoDisableRoodRequest implements Serializable {

    public String getMachineCode() {
        return MachineCode;
    }

    public void setMachineCode(String machineCode) {
        MachineCode = machineCode;
    }

    public List<Integer> getRoadIDs() {
        return RoadIDs;
    }

    public void setRoadIDs(List<Integer> roadIDs) {
        RoadIDs = roadIDs;
    }

    /**
     * 设备编号
     */
    private String MachineCode;

    /**
     * 需要禁用的货道ID数组
     */
    private List<Integer> RoadIDs;

}
