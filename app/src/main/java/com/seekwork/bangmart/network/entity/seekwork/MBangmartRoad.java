package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;

public class MBangmartRoad implements Serializable {


    /**
     * 货道ID
     */
    private int RoadID;

    /**
     * 商品ID
     */
    private int ProductID;

    /**
     * 商品名称
     */
    private String ProductName;

    /**
     * 商品分类Code
     */
    private String DisplayType;

    /**
     * 商品分类名称
     */
    private String DisplayTypeName;

    /**
     * 图片地址路劲
     */
    private String PicName;

    /**
     * 容量
     */
    private int Capacity;

    /**
     * 当前库存
     */
    private int Qty;

    public int getRoadID() {
        return RoadID;
    }

    public void setRoadID(int roadID) {
        RoadID = roadID;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getDisplayType() {
        return DisplayType;
    }

    public void setDisplayType(String displayType) {
        DisplayType = displayType;
    }

    public String getDisplayTypeName() {
        return DisplayTypeName;
    }

    public void setDisplayTypeName(String displayTypeName) {
        DisplayTypeName = displayTypeName;
    }

    public String getPicName() {
        return PicName;
    }

    public void setPicName(String picName) {
        PicName = picName;
    }

    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public int getQty() {
        return Qty;
    }

    public void setQty(int qty) {
        Qty = qty;
    }

    public boolean isDis() {
        return IsDis;
    }

    public void setDis(boolean dis) {
        IsDis = dis;
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

    public int getNum() {
        return Num;
    }

    public void setNum(int num) {
        Num = num;
    }

    /**
     * 是否禁用 (true 禁用; false 未禁用)
     */
    private boolean IsDis;

    /**
     * 区域
     */
    private int Area;

    /**
     * 层
     */
    private int Floor;

    /**
     * 货道
     */
    private int Num;


}
