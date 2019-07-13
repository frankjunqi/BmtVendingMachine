package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;
import java.util.List;

public class MRecoverRoadStateRequest implements Serializable {

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
     * 需要开启的货道ID
     */
    private List<Integer> RoadIDs;

}
