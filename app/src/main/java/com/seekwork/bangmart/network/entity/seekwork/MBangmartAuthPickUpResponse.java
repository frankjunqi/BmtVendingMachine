package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;
import java.util.List;

public class MBangmartAuthPickUpResponse implements Serializable {

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int orderID) {
        OrderID = orderID;
    }

    public boolean isAuth() {
        return IsAuth;
    }

    public void setAuth(boolean auth) {
        IsAuth = auth;
    }

    public List<String> getExceptionMsg() {
        return ExceptionMsg;
    }


    public String getExceptionMsgStr() {
        String str = "";
        for (int i = 0; ExceptionMsg != null && i < ExceptionMsg.size(); i++) {
            str = str + ExceptionMsg.get(i) + "\n";
        }
        return str;
    }

    public void setExceptionMsg(List<String> exceptionMsg) {
        ExceptionMsg = exceptionMsg;
    }

    public boolean isHasStockException() {
        return IsHasStockException;
    }

    public void setHasStockException(boolean hasStockException) {
        IsHasStockException = hasStockException;
    }

    /**
     * 订单ID
     */
    private int OrderID;

    /**
     * 是否验证通过: true  没有Msg信息 ; false 有Msg信息
     */
    private boolean IsAuth;

    /**
     * 异常信息
     */
    private List<String> ExceptionMsg;

    /**
     * 其中的错误是否包含库存异常
     */
    private boolean IsHasStockException;
}
