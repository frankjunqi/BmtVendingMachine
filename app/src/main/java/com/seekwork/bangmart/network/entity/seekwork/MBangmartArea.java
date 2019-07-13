package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;
import java.util.List;

public class MBangmartArea implements Serializable {

    private int Area;

    public int getArea() {
        return Area;
    }

    public void setArea(int area) {
        Area = area;
    }

    public List<MBangmartFloor> getmBangmartFloors() {
        return mBangmartFloors;
    }

    public void setmBangmartFloors(List<MBangmartFloor> mBangmartFloors) {
        this.mBangmartFloors = mBangmartFloors;
    }

    private List<MBangmartFloor> mBangmartFloors;

}
