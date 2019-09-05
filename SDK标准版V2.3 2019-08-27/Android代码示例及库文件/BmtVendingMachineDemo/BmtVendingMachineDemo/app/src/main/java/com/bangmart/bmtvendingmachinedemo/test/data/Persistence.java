package com.bangmart.bmtvendingmachinedemo.test.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 目前只做了单一主键的表操作
 * Created by gongtao on 2017/11/19.
 */

public abstract class Persistence {

    private String tableName;
    private String primaryKey;

    private ContentValues rowData;

    public Persistence(String tableName, String primaryKey) {
        this(tableName, primaryKey, new ContentValues());
    }

    public Persistence(String tableName, String primaryKey, ContentValues contentValues) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        rowData = contentValues;
    }

    /**
     * save 保存数据到数据库
     *
     * @param db 数据库操作对象。如果为空，则表示当前的操作是一个新的独立事务。如果不为空，则该数据库操作对象的事务在一起。
     */
    public void save(SQLiteDatabase db) {
        boolean isIndependent = false;
        if (db == null) {
            DBHelper dbHelper = DBHelper.getInstance();
            db = dbHelper.getReadableDatabase();
            //如果没有传入DB的操作对象，则事务是隔离的
            isIndependent = true;
        }

        onBeforeSave();

        List<ContentValues> res = query(primaryKey + " = ?", new String[]{rowData.getAsString(this.primaryKey)});
        boolean isExisting = (res.size()>0);

        try {
            //如果事务是隔离的，则开启一个事务；
            if (isIndependent) {
                db.beginTransaction();
            }

            if (!isExisting) {
                db.insert(tableName, null, rowData);
            } else {
                db.update(tableName, rowData, primaryKey + " = ?", new String[]{rowData.getAsString(this.primaryKey)});
            }

            if (isIndependent) {
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            onError(-1, e.getMessage());
        } finally {
            if (isIndependent) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public void delete(SQLiteDatabase db) {
        boolean isIndependent = false;
        if (db == null) {
            DBHelper dbHelper = DBHelper.getInstance();
            db = dbHelper.getReadableDatabase();
            //如果没有传入DB的操作对象，则事务是隔离的
            isIndependent = true;
        }

        try {
            //如果事务是隔离的，则开启一个事务；
            if (isIndependent) {
                db.beginTransaction();
            }

            db.delete(tableName, primaryKey + " = ?", new String[]{rowData.getAsString(this.primaryKey)});

            if (isIndependent) {
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            onError(-1, e.getMessage());
        } finally {
            if (isIndependent) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public void setField(String name, Object value) {
        if (value instanceof String) {
            rowData.put(name, (String) value);
        } else if (value instanceof byte[]) {
            rowData.put(name, (byte[]) value);
        } else if (value instanceof Float) {
            rowData.put(name, (Float) value);
        } else if (value instanceof Double) {
            rowData.put(name, (Double) value);
        } else if (value instanceof Short) {
            rowData.put(name, (Short) value);
        } else if (value instanceof Integer) {
            rowData.put(name, (Integer) value);
        } else if (value instanceof Long) {
            rowData.put(name, (Long) value);
        }
    }

    public Object getField(String name) {
        return rowData.get(name);
    }

    public int load(String valueOfPrimaryKey) {
        List<ContentValues> res;

        res = query(this.primaryKey + " = ?", new String[]{valueOfPrimaryKey});

        if (res.size() == 0) {
            return 0;
        }

        ContentValues cv = res.get(0);
        this.rowData = cv;

        return 1;
    }

    public List<ContentValues> query(String criteria, String[] values) {
        DBHelper dbHelper = DBHelper.getInstance();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<ContentValues> res = new ArrayList<>();

        Cursor c = db.query(tableName, null, criteria, values, null, null, null);
        int colCount = c.getColumnCount();

        if (c != null && c.getCount() > 0 && c.moveToFirst()) {
            while (!c.isAfterLast()) {
                ContentValues row = new ContentValues();
                for (int col = 0; col < colCount; col++) {
                    switch (c.getType(col)) {
                        case Cursor.FIELD_TYPE_STRING:
                            row.put(c.getColumnName(col), c.getString(col));
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            row.put(c.getColumnName(col), c.getInt(col));
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            row.put(c.getColumnName(col), c.getFloat(col));
                            break;
                        case Cursor.FIELD_TYPE_BLOB:
                            row.put(c.getColumnName(col), c.getBlob(col));
                            break;
                    }
                }
                res.add(row);
                c.moveToNext();

            }
        }
        c.close();
        db.close();

        return res;
    }

    public String getTableName() {
        return tableName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public ContentValues getFields() {
        return rowData;
    }

    public void setFields(ContentValues rowData) {
        this.rowData = rowData;
    }

    public abstract void onBeforeSave();

    public abstract void onafterLoad();

    public abstract void onError(int type, String desc);

}
