package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;
import java.util.ArrayList;
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


    private List<MBangmartRoad> allBangmartRoads;

    public List<MBangmartRoad> getAllBangmartRoads() {
        if (allBangmartRoads == null) {
            allBangmartRoads = new ArrayList<>();
        } else {
            return allBangmartRoads;
        }
        for (int i = 0; i < mBangmartFloors.size(); i++) {
            allBangmartRoads.addAll(mBangmartFloors.get(i).getmBangmartRoads());
        }
        return allBangmartRoads;
    }

}
