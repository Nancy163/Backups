package net.USky.util;


import net.USky.entity.UserInfo;
import net.USky.util.SqliteHelper.ConstantUtil;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqliteUtil {
	private SqliteHelper helper;

	public SqliteUtil(Context context) {
		helper = new SqliteHelper(context);
	}

	public void insert(String uname, String utel) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ConstantUtil.USER_NAME, uname);
		values.put(ConstantUtil.USER_TEL, utel);
		db.insert(ConstantUtil.TABLE_NAME, null, values);
		db.close();
	}

	public UserInfo query() {
		SQLiteDatabase db = helper.getReadableDatabase();
		UserInfo info = null;
		Cursor cursor = db.query(ConstantUtil.TABLE_NAME, null, null, null,
				null, null, null);
		while (cursor != null && cursor.moveToNext()) {
			info = new UserInfo();
			String name = cursor.getString(cursor
					.getColumnIndex(ConstantUtil.USER_NAME));
			String tel = cursor.getString(cursor
					.getColumnIndex(ConstantUtil.USER_TEL));
			info.setUsername(name);
			info.setTelphone(tel);
		}
		cursor.close();
		return info;
	}
}
