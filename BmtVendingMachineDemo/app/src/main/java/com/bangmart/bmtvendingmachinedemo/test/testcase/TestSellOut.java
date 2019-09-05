package com.bangmart.bmtvendingmachinedemo.test.testcase;

/**
 * Created by gongtao on 2017/12/23.
 */

public class TestSellOut {
    private static String[] locations = new String[]{"010001", "010402", "010201", "000002", "000200"};

    public static String getNextLocation(){
        int idx = (int)(Math.random() * 10);
        if (idx < locations.length) {
            return locations[idx];
        } else {
            return null;
        }
    }
}
