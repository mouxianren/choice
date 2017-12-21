package com.yiqi.choose.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.yiqi.choose.impl.BasicDaoImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moumou on 17/12/13.
 */

public class HistoryDao extends BasicDaoImpl{
    public static HistoryDao instance;
    public HistoryDao(Context context) {
        super(context);
    }
    public static HistoryDao getInstance(Context context) {
        if (instance == null) {
            instance = new HistoryDao(context);
        }
        return instance;
    }
    public void saveSS(String history_ci) {

        try {
            String sqlStr = "INSERT INTO HISTORY(HISTORY_CI,TIME)VALUES(?,?)";
            sqlDataBase.execSQL(
                    sqlStr,
                    new Object[] {history_ci,System.currentTimeMillis()});
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public void deteAllGoods() {
        try {
            sqlDataBase.delete("HISTORY", null, null);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void deteCI(String name) {
        try {
            String sqlStr = "DELETE FROM HISTORY WHERE HISTORY_CI=?";
            sqlDataBase.execSQL(sqlStr, new Object[] { name });
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /***
     * 查询数据库中是否有此项商品
     */
    /** 根据计划id，卡号查询表里面有没有这个卡号 */
    public Integer queryGoods(String id) {
        int total = 0;
        Cursor cursor = null;
        try {
            String sqlStr = "select count(*) from HISTORY where HISTORY_CI=?";

            cursor = sqlDataBase.rawQuery(sqlStr, new String[] { id });
            if (cursor.moveToNext()) {
                total = cursor.getInt(0);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return total;
    }

    /**
     * 查询全部的收藏信息
     *
     * */

    public List<String> queryGoods() {
        List<String> list = new ArrayList<String>();
        sqlDataBase.beginTransaction();
        Cursor cursor = null;
        try {
            String sqlStr = "SELECT HISTORY_CI FROM HISTORY  ORDER BY TIME DESC LIMIT 20";
            cursor = sqlDataBase.rawQuery(sqlStr, null);
            while (cursor.moveToNext()) {
                String name=cursor.getString(cursor.getColumnIndex("HISTORY_CI"));
                list.add(name);
            }
            sqlDataBase.setTransactionSuccessful();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            sqlDataBase.endTransaction();
            closeCursor(cursor);
        }
        return list;
    }
    public int getAllNumber(){
        int total = 0;
        Cursor cursor = null;
        try {
            String sqlStr = "select count(*) from HISTORY ";

            cursor = sqlDataBase.rawQuery(sqlStr,null);
            cursor.moveToFirst();

            total = cursor.getInt(0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return total;

    }

}
