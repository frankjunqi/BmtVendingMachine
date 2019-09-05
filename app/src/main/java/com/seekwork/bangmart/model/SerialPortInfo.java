package com.seekwork.bangmart.model;

/**
 * Created by zhoujq on 2017/12/14.
 * 串口信息
 */

public class SerialPortInfo {
    /**
     * 对应com口ID
     */
    private int comId;

    /**
     * 波特率
     */
    private int baudRate;

    /**
     * Android串口端口设备名称 由Android厂商提供或自测
     * 如"/dev/ttyS0"
     */
    private String serialPortDeviceName;

    public SerialPortInfo() {

    }

    public SerialPortInfo(int comId, int baudRate, String serialPortDeviceName) {
        this.comId = comId;
        this.baudRate = baudRate;
        this.serialPortDeviceName = serialPortDeviceName;
    }

    public int getComId() {
        return comId;
    }

    public void setComId(int comId) {
        this.comId = comId;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public String getSerialPortDeviceName() {
        return serialPortDeviceName;
    }

    public void setSerialPortDeviceName(String serialPortDeviceName) {
        this.serialPortDeviceName = serialPortDeviceName;
    }
}
