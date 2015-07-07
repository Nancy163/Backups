package net.USky.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private SQLiteDatabase db;

	public DBHelper(Context context) {
		super(context, "first.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	// ���ݿ��ʼ��
	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;

		db.execSQL("create table U_tb ( id integer(10) primary key auto increment,name varchar(20),phone varchar(20),token varchar(20))");
	}

	// �������ݿ�汾
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	// ��������
	public void insert(String name, String phone, String token) {
		db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("phone", phone);
		values.put("token", token);
		db.insert("U_tb", null, values);
	}

	// ��ѯ����
	public Cursor select() {
		db = getReadableDatabase();
		Cursor cursor = db.query("U_tb", null, null, null, null, null,null);
		while(cursor.moveToNext()){
			
		}

		return cursor;
	}
}
