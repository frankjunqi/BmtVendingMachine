package com.seekwork.bangmart.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gongtao on 2017/11/18.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BosNT.db";
    private static final int DATABASE_VERSION = 1;
    private static DBHelper dbHelper;

    private static final String STORAGE_LOCATION_STRUCT = "CREATE TABLE IF NOT EXISTS STORAGE_LOCATION " +
            "(mac_id STRING PRIMARY KEY, struct BLOB, coordinate BLOB)";

    private DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void create(Context ctx){
        if (dbHelper == null) {
            dbHelper = new DBHelper(ctx);
        }
    }

    public static DBHelper getInstance(){
        return dbHelper;
    }


    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(STORAGE_LOCATION_STRUCT);
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void truncat(String tableName) {
        dbHelper.getWritableDatabase().execSQL("delete from " + tableName);
    }
}
