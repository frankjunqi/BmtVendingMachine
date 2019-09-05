package com.bangmart.nt;

import com.bangmart.nt.command.*;
import com.bangmart.nt.common.constant.Constant;
import com.bangmart.nt.common.model.Location;
import com.bangmart.nt.common.util.DataUtil;
import com.bangmart.nt.machine.Machine;
import com.bangmart.nt.sys.ByteBuffer;
import com.bangmart.nt.vendingmachine.model.*;

import java.util.ArrayList;
import java.util.List;

import static com.bangmart.nt.command.CommandDef.*;

public class Tester {

    public static void main(String[] args) {
        String serialPort = "/dev/ttyAMA0";

        if (args.length >= 1 && args[0].startsWith("/dev")) {
            serialPort = args[0];
        }

        Machine machine = Machine.create("1234567890", serialPort);

        String log=null;
        /**
         * start: location scan
         */
        Machine idleMachine= waitIdleMachineBySync(machine);
        if(null!=idleMachine){
            Command cmd=Command.create(CommandDef.ID_LOCATION_SCAN,null,Command.TYPE_ASYNC);
            cmd.setOnCompleteListener(new ICallbackListener() {
                @Override
                public boolean callback(Command cmd) {

                    String log=null;
                    if(cmd.isReplyTimeout()||cmd.isTaskTimeout()){
                        log="location scan cmd.isReplyTimeout()||cmd.isTaskTimeout()";
                        System.out.println(log);
                        return true;

                    }

                    if(null==cmd.getResult()){
                        log="null==cmd.getResult()";
                        System.out.println(log);
                        return true;
                    }

                    byte[] data=cmd.getResult().getData();
                    if(null==data){
                        log="null==cmd.getResult().getData()";
                        System.out.println(log);
                        return true;
                    }

                    log= DataUtil.ByteArrToHexString(data);
                    System.out.println(log);

                    RspGenerateLocationCoordinateData rspGenerateLocationCoordinateData=RspGenerateLocationCoordinateData.valueOf(data);
                    if(null!=rspGenerateLocationCoordinateData){
                        log="rspGenerateLocationCoordinateData:"+rspGenerateLocationCoordinateData.toString();
                        System.out.println(log);
                    }

                    return true;
                }
            });

            idleMachine.executeCommand(cmd);
            System.out.println("execute location scan cmd");
        }else {
            System.out.println("Can not execute location scan cmd,just for machine is not idle.");
        }
        /**
         * end: location scan
         */



        /********************************start:查询设备参数（0x23）(同步)********************************/
         idleMachine= waitIdleMachineBySync(machine);

        if(null!=idleMachine){
            /**
             *1.构造命令(同步)
             */
            Command cmd = Command.create(CommandDef.ID_GET_MAC_PARAMETER, Command.TYPE_SYNC);

            /**
             * 2.执行命令
             */
            idleMachine.executeCommand(cmd);
            System.out.println("execute get device parameter cmd");

            /**
             * 3.解析命令响应数据
             */
            if (cmd.isError()){
                log=cmd.getErrorDescription();
                System.out.println(log);
            }

            if(null!=cmd.getResult()) {
                byte[] data = cmd.getResult().getData();
                if (null != data) {
                    log= DataUtil.ByteArrToHexString(data);
                    System.out.println(log);
                    RspGetDeviceParameter rspGetDeviceParameter = RspGetDeviceParameter.valueOf(data);
                    if(null!=rspGetDeviceParameter) {
                        log = "rspGetDeviceParameter:"+rspGetDeviceParameter.toString();
                        System.out.println(log);
                    }
                }
            }
        }else {
            System.out.println("machine is not idle.");
        }


        /********************************end:查询设备参数（0x23）(同步)********************************/


        /********************************start:查询货道坐标列表（0x24）(异步)********************************/
        idleMachine= waitIdleMachineBySync(machine);

        if(null!=idleMachine){
            /**
             *1.构造命令(异步)
             */
            Command cmd = Command.create(CommandDef.ID_GET_LOCATION_DATA, null, Command.TYPE_ASYNC);
            /**
             * 2.监听相关回调
             */
            cmd.setOnCompleteListener(new ICallbackListener() {
                @Override
                public boolean callback(Command cmd) {
                    String log=null;
                    if (cmd.isReplyTimeout() || cmd.isTaskTimeout()) {
                        return true;
                    }

                    if(null!=cmd.getResult()){
                        byte[] data = cmd.getResult().getData();
                        if(null!=data){
                            RspGetLocationCoordinateData rspGetLocationCoordinateData = RspGetLocationCoordinateData.valueOf(data);
                            if (null != rspGetLocationCoordinateData) {
                                //TODO  1.解析货道坐标
                                //TODO  2.货柜宽度=右极限+托盘外部宽度(300mm),货柜高度=顶部极限+托盘外部高度(210mm)
                                //TODO  3.根据1和2中数据解析货道宽度和高度
                                log=rspGetLocationCoordinateData.toString();
                                System.out.println(log);

                            }
                        }

                    }
                    return true;
                }
            });

            /**
             * 3.执行命令
             */
            idleMachine.executeCommand(cmd);
        }else {
            System.out.println("Can not execute get location data cmd,just for machine is not idle.");
        }

        /********************************end:查询货道坐标列表（0x24）(异步)********************************/





        /********************************start: 出货（0x04）(异步)********************************/
        /**
         * 1.生成出货业务数据(一组出货最多出三个货道,如果单组出货超过一个货道,必须遵循智能出货规则,如果单组出货不超过一个则无需遵循智能出货规则)
         */
            List<Location> locations=new ArrayList<>();
            Location location1=new Location(0,0,0);
            locations.add(location1);
            /*locations.add(location2);
            locations.add(location3);*/
            final ReqOutGoods reqOutGoods = new ReqOutGoods(locations);
            final byte[] data = ReqOutGoods.valueOf(reqOutGoods);

        /**
         * 2.构造出货命令(异步)
         */
            Command cmd = Command.create(CommandDef.ID_SELLOUT, data, Command.TYPE_ASYNC);
            //cmd.setDesc(businessInfo);


        /**
         * 3.监听相关回调
         */
            cmd.setOnReplyListener(new ICallbackListener() {
                @Override
                public boolean callback(Command cmd) {
                    String log=null;
                    Attention attention = cmd.getLastAttentionData();
                    //出货失败，需要补发一条命令。
                    byte[] attData = attention.getData();
                    switch (attData[0]) {
                        case ATT_SELLOUT_NOT_TAKEN_AWAY:
                            log="上个用户没有把货取走，请先取走商品再继续出货";
                            System.out.println(log);
                            break;
                        case ATT_SELLOUT_PICK_UP_COMPLETE:
                            log="第" + attData[1] + "个商品取货成功。";
                            System.out.println(log);
                            break;
                        case ATT_SELLOUT_PICK_UP_FAILED:
                            ByteBuffer bb = new ByteBuffer(5);
                            log="第" + attData[1] + "个商品取货失败。";
                             System.out.println(log);
                            int outGoodsFailedIndex = attData[1] & Constant.BYTE_MASK;
                            Location retryLocation = null;//calcRetryLocation(outGoodsFailedIndex);
                            if (null == retryLocation) {
                                bb.append(ATT_SELLOUT_PICK_UP_NO_OPTION);
                                byte[] retry = cmd.sendAttention(bb.copy());
                                log="无重试货道";
                                System.out.println(log);
                            } else {

                                byte area=(byte) retryLocation.getArea();
                                byte floor=(byte) retryLocation.getFloor();
                                byte location=(byte) retryLocation.getLocation();

                                log="retry location (are,floor,location)=("+retryLocation.getArea()+","+retryLocation.getFloor()+","+retryLocation.getLocation()+")";
                                System.out.println(log);
                                bb.append(ATT_SELLOUT_PICK_UP_RETRY).append(attData[1]).append(area).append(floor).append(location);
                                byte[] retry = cmd.sendAttention(bb.copy());
                                if (null != retry) {
                                    //Logger.info(TAG, "重试指令:" + DataUtil.bytesToHexString(retry));
                                    System.out.println(log);
                                }


                            }


                            break;
                        case ATT_SELLOUT_WAIT_TO_GET_AWAY:
                            log="取货口已打开，等待用户取走。";
                            System.out.println(log);
                            break;
                        default:
                    }
                    return true;
                }
            });


            cmd.setOnCompleteListener(new ICallbackListener() {
                @Override
                public boolean callback(Command cmd) {
                    Response response = cmd.getResult();
                    if(null!=response){
                        RspOutGoods rspOutGoods = RspOutGoods.valueOf(response.getData());
                        //TODO 根据出货结果,进行其它业务处理
                    }


                    return true;
                }
            });


            cmd.setOnErrorListener(new ICallbackListener() {
                @Override
                public boolean callback(Command cmd) {

                    Response response = cmd.getResult();
                    if(null!=response){
                        RspOutGoods rspOutGoods = RspOutGoods.valueOf(response.getData());
                        //TODO 根据出货结果,进行其它业务处理
                    }

                    return true;
                }
            });

            /**
             * 4.执行出货命令
             */
            machine.executeCommand(cmd);
        /********************************end: 出货（0x04）(异步)********************************/

    }

    /**
     * 等待空闲设备(同步,耗时)
     * @return
     */
    public static Machine waitIdleMachineBySync(final Machine machine){
        long waitTimeMs=20*1000; //20s
        long intervalDetectionTimeMs= 1*1000;//1s
        return waitIdleMachineBySync(machine,waitTimeMs,intervalDetectionTimeMs);
    }

    /**
     * 等待空闲设备(同步,耗时)
     * @param waitTimeMs 最大等待时间ms
     * @param intervalDetectionTimeMs 间隔检测时间ms
     * @return
     */
    private static Machine waitIdleMachineBySync( final Machine machine,long waitTimeMs, long intervalDetectionTimeMs){
        Machine idleMachine=null;
        String log=null;
        /**
         * 最大+最小等待时间
         */
        long MAX_WAIT_TIME_MS=100*1000;//100s
        long MIN_WAIT_TIME_MS=0;//0s

        long curWaitTimeMs=MAX_WAIT_TIME_MS;
        if(waitTimeMs>=MIN_WAIT_TIME_MS&&waitTimeMs<=MAX_WAIT_TIME_MS){
            curWaitTimeMs=waitTimeMs;
        }

        /**
         * 最大+最小间隔检测时间
         */
        long MAX_INTERVAL_DETECTION_TIME_MS=10*1000; //10s
        long MIN_INTERVAL_DETECTION_TIME_MS=500;//0.5s
        long curIntervalDetectionTimeMs=MIN_INTERVAL_DETECTION_TIME_MS;
        if(intervalDetectionTimeMs>=MIN_INTERVAL_DETECTION_TIME_MS&&intervalDetectionTimeMs<=MAX_INTERVAL_DETECTION_TIME_MS){
            curIntervalDetectionTimeMs=intervalDetectionTimeMs;
        }

        int curTryCount=0;
        /**
         * 耗时
         */
        long consumingTimeMs=0;
        long startTimeMs= System.currentTimeMillis();
        do {
            curTryCount++;
            consumingTimeMs= System.currentTimeMillis()-startTimeMs;

            if(true==machine.isIdleState()){
                idleMachine=machine;
                break;
            }else {
                log = "["+curTryCount+"]"+"wait machine idle,total consuming time Ms["+consumingTimeMs+"]ms";
                System.out.println(log);
            }


            if(consumingTimeMs<=curWaitTimeMs) {
                try {
                    Thread.sleep(curIntervalDetectionTimeMs);
                } catch (InterruptedException ex) {

                }
            }

        }while (consumingTimeMs<=curWaitTimeMs);


        return idleMachine;
    }


}
