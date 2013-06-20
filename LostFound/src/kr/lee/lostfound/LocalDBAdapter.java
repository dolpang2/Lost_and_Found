package kr.lee.lostfound;

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
	public static final String KEY_OPTION_IS_LOCKED = "isLocked";

	public static final String DATABASE_NAME = "lostfound";
	public static final String TABLE_MEMBER_NAME = "member";
	public static final String TABLE_OPTION_NAME = "option";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String TABLE_MEMBER_CREATE_QUERY = "create table if not exists " + TABLE_MEMBER_NAME
			+ " (" + KEY_MEMBER_BIRTH + " date not null, " + KEY_MEMBER_MAIL + " text primary key not null, "
			+ KEY_MEMBER_PASS + " text not null);";

	private static final String TABLE_OPTION_CREATE_QUERY = "create table if not exists " + TABLE_OPTION_NAME
			+ " (" + KEY_OPTION_USE_SIREN + " bool not null, " + KEY_OPTION_USE_GPS + " bool not null, "
			+ KEY_OPTION_USE_CAMERA + " bool not null, " + KEY_OPTION_USE_LOCK + " bool not null, "
			+ KEY_OPTION_USE_LOCKFAIL + " bool not null, " + KEY_OPTION_USE_BACKUP + " bool not null, "
			+ KEY_OPTION_TTS_MESSAGE + " text primary key not null, " + KEY_OPTION_IS_LOCKED
			+ " bool not null);";

	private static final int DATABASE_VERSION = 3;

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

	public long createMember(String birth, String mail, String pass) throws SQLException {
		ContentValues row = new ContentValues();
		row.put(KEY_MEMBER_BIRTH, birth);
		row.put(KEY_MEMBER_MAIL, mail);
		row.put(KEY_MEMBER_PASS, pass);

		long rowID = mDb.insert(TABLE_MEMBER_NAME, null, row);
		return rowID;
	}

	public int deleteAllMember() throws SQLException {
		int affectedRow = mDb.delete(TABLE_MEMBER_NAME, null, null);
		return affectedRow;
	}

	public Cursor selectAllMember() throws SQLException {
		Cursor cursor;
		cursor = mDb.query(TABLE_MEMBER_NAME, new String[] { KEY_MEMBER_BIRTH, KEY_MEMBER_MAIL,
				KEY_MEMBER_PASS }, null, null, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}

	public void createOption() throws SQLException {
		ContentValues row = new ContentValues();
		row.put(KEY_OPTION_USE_SIREN, true);
		row.put(KEY_OPTION_USE_GPS, true);
		row.put(KEY_OPTION_USE_CAMERA, true);
		row.put(KEY_OPTION_USE_LOCK, true);
		row.put(KEY_OPTION_USE_LOCKFAIL, true);
		row.put(KEY_OPTION_USE_BACKUP, true);
		row.put(KEY_OPTION_TTS_MESSAGE, "010-8266-8969");
		row.put(KEY_OPTION_IS_LOCKED, false);

		mDb.insert(TABLE_OPTION_NAME, null, row);
	}

	public int deleteOption() throws SQLException {
		int affectedRow = mDb.delete(TABLE_OPTION_NAME, null, null);
		return affectedRow;
	}

	public void updateOption(ContentValues updateData) throws SQLException {
		mDb.update(TABLE_OPTION_NAME, updateData, null, null);
	}

	public Cursor selectAllOption() throws SQLException {
		Cursor cursor;
		cursor = mDb.query(TABLE_OPTION_NAME, new String[] { KEY_OPTION_USE_SIREN, KEY_OPTION_USE_GPS,
				KEY_OPTION_USE_CAMERA, KEY_OPTION_USE_LOCK, KEY_OPTION_USE_LOCKFAIL, KEY_OPTION_USE_BACKUP },
				null, null, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}

	public void updateSirenMessage(String message) throws SQLException {
		ContentValues row = new ContentValues();
		row.put(KEY_OPTION_TTS_MESSAGE, message);

		mDb.update(TABLE_OPTION_NAME, row, null, null);
	}

	public Cursor selectSirenMessage() throws SQLException {
		Cursor cursor;
		cursor = mDb.query(TABLE_OPTION_NAME, new String[] { KEY_OPTION_TTS_MESSAGE }, null, null, null,
				null, null);
		cursor.moveToFirst();

		return cursor;
	}

	public boolean getIsLocked() throws SQLException {
		Cursor cursor;
		cursor = mDb.query(TABLE_OPTION_NAME, new String[] { KEY_OPTION_IS_LOCKED }, null, null, null, null,
				null);
		cursor.moveToFirst();
		Boolean result = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_IS_LOCKED)) > 0;

		return result;
	}
	
	public void setIsLocked(Boolean isLocked) throws SQLException {
		ContentValues row = new ContentValues();
		row.put(KEY_OPTION_IS_LOCKED, isLocked);

		mDb.update(TABLE_OPTION_NAME, row, null, null);
		
	}
	
	public void setPassword(String pass) throws SQLException {
		ContentValues row = new ContentValues();
		row.put(KEY_MEMBER_PASS, pass);

		mDb.update(TABLE_MEMBER_NAME, row, null, null);
	}
}
