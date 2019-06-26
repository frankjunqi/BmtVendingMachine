package com.seekwork.bangmart;

import java.util.List;

import com.bangmart.nt.command.Command;

import static com.bangmart.nt.command.CommandDef.ID_DEVICE_INIT;
import static com.bangmart.nt.command.CommandDef.ID_LOCATION_SCAN;
import static com.bangmart.nt.command.CommandDef.ID_PRE_PICKUP;
import static com.bangmart.nt.command.CommandDef.ID_SELLOUT;

/**
 * 用于生成控制命令。
 * 这个类只是用于这个测试系统。在真实环境中，应重写这个类。
 *
 * @author gongtao on 2017/11/12.
 */

public class CommandFactory {

    public static Command createCommand4DeviceInit(String devId, long replyTimeout, long responseTimeout) {
        return Command.create(ID_DEVICE_INIT, devId.getBytes(), Command.TYPE_ASYNC, replyTimeout, responseTimeout);
    }

    public static Command createCommand4LocationScan(long replyTimeout, long responseTimeout) {
        return Command.create(ID_LOCATION_SCAN, null, Command.TYPE_ASYNC, replyTimeout, responseTimeout);
    }

    public static Command createCommand4prePickup(byte area, byte floor, byte location, long replyTimeout, long responseTimeout) {
        byte[] params = new byte[3];
        params[0] = area;
        params[1] = floor;
        params[2] = location;

        return Command.create(ID_PRE_PICKUP, params, Command.TYPE_ASYNC, replyTimeout, responseTimeout);
    }

    public static Command createCommand4SellOut(List<byte[]> params, int mode, long replyTimeout, long responseTimeout) {
        int goodsCount = params.size();

        byte[] paramCode = new byte[goodsCount * 4 + 2];
        paramCode[0] = (byte)mode;
        paramCode[1] = (byte)params.size();

        for (int i=0; i < goodsCount; i++) {
            byte[] good = params.get(i);
            paramCode[2 + i * 4] = (byte)i;
            paramCode[3 + i * 4] = good[0];
            paramCode[4 + i * 4] = good[1];
            paramCode[5 + i * 4] = good[2];
        }

        return Command.create(ID_SELLOUT, paramCode, Command.TYPE_ASYNC, replyTimeout, responseTimeout);
    }
}
