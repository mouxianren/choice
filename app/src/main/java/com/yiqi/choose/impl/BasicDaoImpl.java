package com.yiqi.choose.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yiqi.choose.db.DBOpenHelper;


public class BasicDaoImpl implements IBasicDao{
	public DBOpenHelper dbOpenHelper;
	public SQLiteDatabase sqlDataBase;  //数据库操作类
	
	public BasicDaoImpl(Context context){
		dbOpenHelper = DBOpenHelper.getInstance(context);
		sqlDataBase= dbOpenHelper.getWritableDatabase();
	
	}
	
	/**
	 * 关闭cursor
	 * @param c
	 */
	public void closeCursor(Cursor c) {
		if (c != null && !c.isClosed()) {
			c.close();
			c = null;
		}
	}

	
}
