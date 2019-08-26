package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;

/**
 * 出货实体
 */
public class TakeOutProductBean implements Serializable {

    private int productID;

    private int Area = 0;
    private int Floor = 0;
    private int Column = 0;

    private int x;
    private int y;


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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


}
