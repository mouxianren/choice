package com.yiqi.choose.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.yiqi.choose.impl.BasicDaoImpl;

public class SSDao extends BasicDaoImpl {
	public static SSDao instance;

	public SSDao(Context context) {
		super(context);
	}

	public static SSDao getInstance(Context context) {
		if (instance == null) {
			instance = new SSDao(context);
		}
		return instance;
	}
	
	public void saveSS(String pid,String quanId) {

		try {
			String sqlStr = "INSERT INTO QUAN(PID,QUANID)VALUES(?,?)";
			sqlDataBase.execSQL(
					sqlStr,
					new Object[] {pid, quanId});
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void deteAllGoods() {
		try {
			sqlDataBase.delete("QUAN", null, null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/***
	 * 查询数据库中是否有此项商品
	 */
	/** 根据计划id，卡号查询表里面有没有这个卡号 */
	public Integer queryPID(String name) {
		int total = 0;
		Cursor cursor = null;
		try {
			String sqlStr = "select count(*) from QUAN where PID=?";
			cursor = sqlDataBase.rawQuery(sqlStr, new String[] {name});
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
	/***
	 * 查询数据库中是否有此项商品
	 */
	/** 根据计划id，卡号查询表里面有没有这个卡号 */
	public Integer queryQuanId(String quanId) {
		int total = 0;
		Cursor cursor = null;
		try {
			String sqlStr = "select count(*) from QUAN where QUANID=?";
			cursor = sqlDataBase.rawQuery(sqlStr, new String[] {quanId});
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
	public void detePID(String name) {
		try {
			String sqlStr = "DELETE FROM QUAN WHERE PID=?";
			sqlDataBase.execSQL(sqlStr, new Object[] { name });
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 查询全部的收藏信息
	 * 
	 * */

	public String queryPid() {

		sqlDataBase.beginTransaction();
		Cursor cursor = null;
		String name="";
		try {
			String sqlStr = "SELECT PID FROM QUAN";
			cursor = sqlDataBase.rawQuery(sqlStr, null);
			while (cursor.moveToNext()) {
				name=cursor.getString(cursor.getColumnIndex("PID"));
				break;
			}


			sqlDataBase.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sqlDataBase.endTransaction();
			closeCursor(cursor);
		}
		return name;
	}

	/**
	 * 查询全部的收藏信息
	 *
	 * */

	public String queryQuanId() {

		sqlDataBase.beginTransaction();
		Cursor cursor = null;
		String name="";
		try {
			String sqlStr = "SELECT QUANID FROM QUAN";
			cursor = sqlDataBase.rawQuery(sqlStr, null);
			while (cursor.moveToNext()) {
				name=cursor.getString(cursor.getColumnIndex("QUANID"));
				break;
			}


			sqlDataBase.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sqlDataBase.endTransaction();
			closeCursor(cursor);
		}
		return name;
	}
}
