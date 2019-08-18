package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;

/**
 * 商品出货详情
 */
public class MBangmarProcPickRoad implements Serializable {

    public int getRoadID() {
        return RoadID;
    }

    public void setRoadID(int roadID) {
        RoadID = roadID;
    }

    public int getOutNum() {
        return OutNum;
    }

    public void setOutNum(int outNum) {
        OutNum = outNum;
    }

    /**
     * 货道ID
     */
    private int RoadID;

    /**
     * 出货数量
     */
    private int OutNum;


    public int getArea() {
        return Area;
    }

    public void setArea(int area) {
        Area = area;
    }

    public int getFloor() {
        return Floor;
    }

    public void setFloor(int floor) {
        Floor = floor;
    }

    public int getColumn() {
        return Column;
    }

    public void setColumn(int column) {
        Column = column;
    }

    private int Area = 0;
    private int Floor =0;
    private int Column = 0;

}
