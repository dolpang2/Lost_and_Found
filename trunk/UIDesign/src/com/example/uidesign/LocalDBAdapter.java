package com.example.uidesign;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDBAdapter {
	public static final String KEY_MEMBER_BIRTH = "birth";
	public static final String KEY_MEMBER_MAIL = "mail";
	public static final String KEY_MEMBER_PASS = "pass";

	public static final String KEY_OPTION_USE_SIREN = "useSiren";
	public static final String KEY_OPTION_USE_GPS = "useGPS";
	public static final String KEY_OPTION_USE_CAMERA = "useCamera";
	public static final String KEY_OPTION_USE_LOCK = "useLock";
	public static final String KEY_OPTION_USE_LOCKFAIL = "useLockFail";
	public static final String KEY_OPTION_USE_BACKUP = "useBackup";
	public static final String KEY_OPTION_TTS_MESSAGE = "TTSMessage";

	public static final String DATABASE_NAME = "lostfound";
	public static final String TABLE_MEMBER_NAME = "member";
	public static final String TABLE_OPTION_NAME = "option";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String TABLE_MEMBER_CREATE_QUERY = "create table if not exists " + TABLE_MEMBER_NAME
			+ " (" + KEY_MEMBER_BIRTH + " date not null, " + KEY_MEMBER_MAIL + " text not null, "
			+ KEY_MEMBER_PASS + " text not null);";

	private static final String TABLE_OPTION_CREATE_QUERY = "create table if not exists " + TABLE_OPTION_NAME
			+ " (" + KEY_OPTION_USE_SIREN + " bool not null, " + KEY_OPTION_USE_GPS + " bool not null, "
			+ KEY_OPTION_USE_CAMERA + " bool not null, " + KEY_OPTION_USE_LOCK + " bool not null, "
			+ KEY_OPTION_USE_LOCKFAIL + " bool not null, " + KEY_OPTION_USE_BACKUP + " bool not null, "
			+ KEY_OPTION_TTS_MESSAGE + " text not null);";

	private static final int DATABASE_VERSION = 1;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_MEMBER_CREATE_QUERY);
			db.execSQL(TABLE_OPTION_CREATE_QUERY);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBER_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPTION_NAME);
			onCreate(db);
		}

		public void onOpen(SQLiteDatabase db) {
			db.execSQL(TABLE_MEMBER_CREATE_QUERY);
			db.execSQL(TABLE_OPTION_CREATE_QUERY);
		}
	}

	public LocalDBAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public LocalDBAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public long createMember(int year, int month, int date, String mail, String pass) throws SQLException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		Date birth = new Date();

		ContentValues row = new ContentValues();
		row.put(KEY_MEMBER_BIRTH, dateFormat.format(birth));
		row.put(KEY_MEMBER_MAIL, mail);
		row.put(KEY_MEMBER_PASS, pass);

		long rowID = mDb.insert(TABLE_MEMBER_NAME, null, row);
		return rowID;
	}

	public int deleteMember() throws SQLException {
		int affectedRow = mDb.delete(TABLE_MEMBER_NAME, null, null);
		return affectedRow;
	}

	public Cursor selectMember() throws SQLException {
		Cursor cursor;
		cursor = mDb.query(TABLE_MEMBER_NAME, new String[] { KEY_MEMBER_BIRTH, KEY_MEMBER_MAIL,
				KEY_MEMBER_PASS }, null, null, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}
}
