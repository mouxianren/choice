package com.yiqi.choose.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	 public static final String dbPath = android.os.Environment
	 .getExternalStorageDirectory().getAbsolutePath();
	 public static final String NAME = dbPath +"/choose/"+ "choose.db";

	//public static String NAME = "baoyougou.db";
	public static int VERSION = 2;
	private static DBOpenHelper instance = null;
	private String FIELDTYPE = " varchar,";
	private String ENDFIELDTYPE = " varchar)";

	public synchronized static DBOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBOpenHelper(context);
		}
		return instance;
	}

	public DBOpenHelper(Context context) {
		super(context, NAME, null, VERSION);
	}






	private static final String QUAN = "QUAN";
	private static final String PID = "PID";
	private static final String QUANID = "QUANID";

	private void createSSTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + QUAN);
		String sql = "CREATE TABLE " + QUAN + "(" + PID + FIELDTYPE+QUANID
				+ ENDFIELDTYPE;
		db.execSQL(sql);
	}

	private static final String HISTORY = "HISTORY";
	private static final String HISTORY_CI = "HISTORY_CI";
	private static final String TIME="TIME";

	private void createSsHistoryTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + HISTORY);
		String sql = "CREATE TABLE " + HISTORY + "(" + HISTORY_CI + FIELDTYPE+TIME
				+ ENDFIELDTYPE;
		db.execSQL(sql);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		createSSTable(db);
		onUpgrade(db,1,2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		createSsHistoryTable(db);
	}


}
