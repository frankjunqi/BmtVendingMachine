package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;

/**
 * 出货实体
 */
public class TakeOutProductBean implements Serializable {

    private int RoadID;
    private int productID;
    private int Area = 0;
    private int Floor = 0;
    private int Column = 0;
    private int width;

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

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


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getRoadID() {
        return RoadID;
    }

    public void setRoadID(int roadID) {
        RoadID = roadID;
    }
}
