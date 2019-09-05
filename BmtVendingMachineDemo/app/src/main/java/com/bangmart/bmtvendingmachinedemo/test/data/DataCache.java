package com.bangmart.bmtvendingmachinedemo.test.data;

import java.util.HashMap;

import com.bangmart.nt.sys.Logger;

/**
 * Created by gongtao on 2017/11/12.
 */

public class DataCache {

    private static final DataCache dc = new DataCache();

    private final HashMap<String, Object> cache = new HashMap<>();

    private DataCache() {

    }

    public static DataCache getInstance() {
        return dc;
    }

    public Object get(String key){
        return cache.get(key);
    }

    public void put(String key, Object obj) {
        cache.put(key, obj);
    }

    public boolean containsKey(String key){
        return cache.containsKey(key);
    }

    public void save(){
        Logger.sLogger4Machine.info("DataCache", "开始保存缓存数据!");

        for (Object obj : cache.values()){
            if (obj instanceof Persistence) {
                Persistence persistence = (Persistence) obj;
                persistence.save(null);
            }
        }

        Logger.sLogger4Machine.info("DataCache", "保存缓存数据完成!");
    }

    public void loadFromDB(){


    }
}
