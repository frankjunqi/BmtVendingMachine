package com.seekwork.bangmart.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gongtao on 2017/11/15.
 */

public class DataStat {

    public static final int STATE_UNKNOW = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_BUSY = 1;
    public static final int STATE_PAUSE = 2;


    private static final DataStat dc = new DataStat();

    private long currentReplyTime, execTime, averageReplyTime = 0, shortReplyTime = 0, longReplyTime = 0;

    private final List<Long> replyTimeHistory = new ArrayList<>();

    private int taskCnt, successTaskCnt = 0, failTaskCnt = 0;

    private long idelTime, busyTime, timestamps;

    private int state = STATE_UNKNOW;

    private long rightTime, recordTime;

    private OnChangeLinstense onchange;

    private Timer csTimer;

    private DataStat() {

    }

    public static DataStat getInstance() {
        return dc;
    }

    public void startStat() {
        idelTime = 0;
        busyTime = 0;
        state = STATE_BUSY;
        timestamps = System.currentTimeMillis();

        currentReplyTime = 0;
        execTime = 0;
        averageReplyTime = 0;
        shortReplyTime = 0;
        longReplyTime = 0;
        replyTimeHistory.clear();

//        successTaskCnt = 0;
//        failTaskCnt = 0;

//        rightTime = 0;
//        recordTime = 0;

        if (csTimer != null) {
            csTimer.cancel();
        }

        csTimer = new Timer();
        csTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (state == STATE_PAUSE) {
                    return;
                }

                execTime += 1;

                if (onchange != null) {
                    onchange.onContinuedSuccess(execTime);
                }
            }
        }, 0, 1000);
    }

    public void pause() {
        long tmp = System.currentTimeMillis();
        if (state == STATE_IDLE) {
            idelTime += (tmp - timestamps);
        }

        if (state == STATE_BUSY) {
            busyTime += (tmp - timestamps);
        }
        timestamps = tmp;
        state = STATE_PAUSE;
    }

    public void resume() {
        long tmp = System.currentTimeMillis();
        timestamps = tmp;
        state = STATE_BUSY;
    }

    public void stop() {
        if (csTimer != null) {
            csTimer.cancel();
        }
        csTimer = null;
        state = STATE_UNKNOW;
    }

    public boolean is(int val) {
        return (state == val);
    }

    public void updateReplyTime(long val) {
        if (val <= 0) {
            return;
        }
        currentReplyTime = val;

        shortReplyTime = (shortReplyTime >= 0 && shortReplyTime < val) ? shortReplyTime : val;
        longReplyTime = (longReplyTime >= 0 && longReplyTime > val) ? longReplyTime : val;

        averageReplyTime = ((averageReplyTime * replyTimeHistory.size()) + val) / (replyTimeHistory.size() + 1);
        replyTimeHistory.add(val);

        if (onchange != null) {
            onchange.onChange(this);
        }
    }

    public void updateTaskCount(int val) {
        taskCnt = val;
        if (onchange != null) {
            onchange.onChange(this);
        }
    }

//    public void decreaseTaskCount() {
//        if (taskCnt <= 0) return;
//        taskCnt -= 1;
//        if (onchange != null) onchange.onChange(this);
//    }

    public void increaseSuccessTaskCount() {
        successTaskCnt += 1;
        if (taskCnt > 0) {
            taskCnt -= 1;
        }

        rightTime += execTime;
        recordTime = (recordTime > rightTime) ? recordTime : rightTime;
        execTime = 0;

        if (onchange != null) {
            onchange.onChange(this);
        }
    }

    public void increaseFailTaskCount() {
        failTaskCnt += 1;
        if (taskCnt > 0) {
            taskCnt -= 1;
        }

        rightTime = 0;
        execTime = 0;

        if (onchange != null) {
            onchange.onChange(this);
        }
    }

    public void setBusy() {
        long tmp = System.currentTimeMillis();
        if (state == STATE_IDLE) {
            idelTime += (tmp - timestamps);
        }
        timestamps = tmp;
        state = STATE_BUSY;
        if (onchange != null) {
            onchange.onChange(this);
        }
    }

    public void setIdle() {
        long tmp = System.currentTimeMillis();
        if (state == STATE_BUSY) {
            busyTime += (tmp - timestamps);
        }
        timestamps = tmp;
        state = STATE_IDLE;
        if (onchange != null) {
            onchange.onChange(this);
        }
    }

    public long getReplyTime() {
        return currentReplyTime;
    }

    public long getExecTime() {
        return execTime;
    }

    public long getAverageReplyTime() {
        return averageReplyTime;
    }

    public long getShortReplyTime() {
        return shortReplyTime;
    }

    public long getLongReplyTime() {
        return longReplyTime;
    }

    public int getTaskCnt() {
        return taskCnt;
    }

    public int getSuccessTaskCnt() {
        return successTaskCnt;
    }

    public int getFailTaskCnt() {
        return failTaskCnt;
    }

    public long getIdleTime() {
        return idelTime;
    }

    public long getBusyTime() {
        return busyTime;
    }

    public long getRightTime() {
        return rightTime;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setOnchange(OnChangeLinstense onchange) {
        this.onchange = onchange;
    }

    public interface OnChangeLinstense {
        public void onChange(DataStat ds);

        public void onContinuedSuccess(long execTime);
        //public void onFail(long recordTime);
    }
}
