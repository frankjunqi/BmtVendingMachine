package com.seekwork.bangmart.model;

import com.bangmart.nt.vendingmachine.model.LocationCoordinate;

import java.io.Serializable;

/**
 * 货道的物理信息数据实体
 * area
 * floor
 * location
 * width
 */
public class SerialLocationBean extends LocationCoordinate implements Serializable {

    private int width;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

}
