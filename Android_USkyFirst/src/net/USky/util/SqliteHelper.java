package net.USky.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {
	@SuppressWarnings("unused")
	private SQLiteDatabase mDatabase;

	public SqliteHelper(Context context) {
		super(context, ConstantUtil.TABLE_NAME, null,
				ConstantUtil.DATABASE_VERSION);
		// TODO Auto-generated constructor stub
		mDatabase = getWritableDatabase();
	}

	/*
	 * 创建表
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		// sql语句
		String sql = "create table " + ConstantUtil.TABLE_NAME + "("
				+ ConstantUtil.USER_ID + " integer primary key,"
				+ ConstantUtil.USER_NAME + " text not null,"
				+ ConstantUtil.USER_TEL + " text not null)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public class ConstantUtil {

		// 数据库名称
		public static final String DATABASE_NAME = "user_manager.db";
		// 数据库版本号
		public static final int DATABASE_VERSION = 1;

		// 表名
		public static final String TABLE_NAME = "user_info";
		// 字段名
		public static final String USER_ID = "uid";
		public static final String USER_NAME = "username";
		public static final String USER_TEL = "telphone";

	}
}
