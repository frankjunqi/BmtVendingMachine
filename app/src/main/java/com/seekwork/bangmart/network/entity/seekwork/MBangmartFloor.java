package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;
import java.util.List;

public class MBangmartFloor implements Serializable {

    private int Floor;

    public int getFloor() {
        return Floor;
    }

    public void setFloor(int floor) {
        Floor = floor;
    }

    public List<MBangmartRoad> getmBangmartRoads() {
        return mBangmartRoads;
    }

    public void setmBangmartRoads(List<MBangmartRoad> mBangmartRoads) {
        this.mBangmartRoads = mBangmartRoads;
    }

    private List<MBangmartRoad> mBangmartRoads;

}
