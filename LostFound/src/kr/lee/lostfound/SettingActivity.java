package kr.lee.lostfound;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class SettingActivity extends ListActivity implements OnClickListener {
	static final int SIREN_INTENT = 0;
	static final int GPS_INTENT = 1;
	static final int CAMERA_INTENT = 2;
	static final int LOCK_INTENT = 3;
	static final int LOCK_FAIL_INTENT = 4;
	static final int BACKUP_INTENT = 5;

	private Button btnConfirm;

	private LocalDBAdapter mDBHelper;

	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> item;
	SimpleAdapter simpleAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		btnConfirm = (Button) findViewById(R.id.setSaveSetting);
		btnConfirm.setOnClickListener(this);

		mDBHelper = new LocalDBAdapter(this);
		mDBHelper.open(); // DB Open

		Cursor cursor = mDBHelper.selectAllOption();
		startManagingCursor(cursor);

		boolean chkSiren = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_SIREN)) > 0;
		boolean chkGPS = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_GPS)) > 0;
		boolean chkCamera = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_CAMERA)) > 0;
		boolean chkLock = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_LOCK)) > 0;
		boolean chkLockFail = cursor.getInt(cursor
				.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_LOCKFAIL)) > 0;
		boolean chkBackup = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_BACKUP)) > 0;

		cursor.close();

		item = new HashMap<String, String>();
		item.put("item1", "경고음 출력 설정");
		if (chkSiren) {
			item.put("item2", "사용함");
		} else {
			item.put("item2", "사용 안함");
		}
		list.add(item);

		item = new HashMap<String, String>();
		item.put("item1", "위치 추적 설정");
		if (chkGPS) {
			item.put("item2", "사용함");
		} else {
			item.put("item2", "사용 안함");
		}
		list.add(item);

		item = new HashMap<String, String>();
		item.put("item1", "전면 카메라 사용 설정");
		if (chkCamera) {
			item.put("item2", "사용함");
		} else {
			item.put("item2", "사용 안함");
		}
		list.add(item);

		item = new HashMap<String, String>();
		item.put("item1", "휴대폰 잠금 설정");
		if (chkLock) {
			item.put("item2", "사용함");
		} else {
			item.put("item2", "사용 안함");
		}
		list.add(item);

		item = new HashMap<String, String>();
		item.put("item1", "잠금해제 실패시 카메라 촬영");
		if (chkLockFail) {
			item.put("item2", "사용함");
		} else {
			item.put("item2", "사용 안함");
		}
		list.add(item);

		item = new HashMap<String, String>();
		item.put("item1", "주소록 백업 설정");
		if (chkBackup) {
			item.put("item2", "사용함");
		} else {
			item.put("item2", "사용 안함");
		}
		list.add(item);

		simpleAdapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
				new String[] { "item1", "item2" }, new int[] { android.R.id.text1, android.R.id.text2 });
		setListAdapter(simpleAdapter);

		btnConfirm = (Button) findViewById(R.id.setSaveSetting);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.setSaveSetting:
			finish();
			break;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		switch (position) {
		case SIREN_INTENT:
			Intent sirenIntent = new Intent(SettingActivity.this, SetSirenMessageActivity.class);
			startActivityForResult(sirenIntent, SIREN_INTENT);
			break;
		case GPS_INTENT:
			Intent gpsIntent = new Intent(SettingActivity.this, LocationSettingActivity.class);
			startActivityForResult(gpsIntent, GPS_INTENT);
			break;
		case CAMERA_INTENT:
			Intent cameraIntent = new Intent(SettingActivity.this, SetSirenMessageActivity.class);
			startActivityForResult(cameraIntent, CAMERA_INTENT);
			break;
		case LOCK_INTENT:
			Intent lockIntent = new Intent(SettingActivity.this, SetSirenMessageActivity.class);
			startActivityForResult(lockIntent, LOCK_INTENT);
			break;
		case LOCK_FAIL_INTENT:
			Intent lockfailIntent = new Intent(SettingActivity.this, SetSirenMessageActivity.class);
			startActivityForResult(lockfailIntent, LOCK_FAIL_INTENT);
			break;
		case BACKUP_INTENT:
			Intent backupIntent = new Intent(SettingActivity.this, ContactsActivity.class);
			startActivityForResult(backupIntent, BACKUP_INTENT);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			ContentValues updateData = new ContentValues();
			boolean result = false;
			switch (requestCode) {
			case SIREN_INTENT:
				result = data.getBooleanExtra("use", false);
				
				updateData.put(LocalDBAdapter.KEY_OPTION_USE_SIREN, result);
				mDBHelper.updateOption(updateData);
				
				item = new HashMap<String, String>();
				item.put("item1", "경고음 출력 설정");
				if (result) {
					item.put("item2", "사용함");
				} else {
					item.put("item2", "사용 안함");
				}
				list.set(SIREN_INTENT, item);
				simpleAdapter.notifyDataSetChanged();
				
				break;
			case GPS_INTENT:
				result = data.getBooleanExtra("use", false);
				
				updateData.put(LocalDBAdapter.KEY_OPTION_USE_GPS, result);
				mDBHelper.updateOption(updateData);
				
				item = new HashMap<String, String>();
				item.put("item1", "위치 추적 설정");
				if (result) {
					item.put("item2", "사용함");
				} else {
					item.put("item2", "사용 안함");
				}
				list.set(GPS_INTENT, item);
				simpleAdapter.notifyDataSetChanged();
				
				break;
			case CAMERA_INTENT:
				result = data.getBooleanExtra("use", false);
				
				updateData.put(LocalDBAdapter.KEY_OPTION_USE_CAMERA, result);
				mDBHelper.updateOption(updateData);
				
				item = new HashMap<String, String>();
				item.put("item1", "전면 카메라 사용 설정");
				if (result) {
					item.put("item2", "사용함");
				} else {
					item.put("item2", "사용 안함");
				}
				list.set(CAMERA_INTENT, item);
				simpleAdapter.notifyDataSetChanged();
				break;
			case LOCK_INTENT:
				result = data.getBooleanExtra("use", false);
				
				updateData.put(LocalDBAdapter.KEY_OPTION_USE_LOCK, result);
				mDBHelper.updateOption(updateData);
				
				item = new HashMap<String, String>();
				item.put("item1", "휴대폰 잠금 설정");
				if (result) {
					item.put("item2", "사용함");
				} else {
					item.put("item2", "사용 안함");
				}
				list.set(LOCK_INTENT, item);
				simpleAdapter.notifyDataSetChanged();
				break;
			case LOCK_FAIL_INTENT:
				result = data.getBooleanExtra("use", false);
				
				updateData.put(LocalDBAdapter.KEY_OPTION_USE_LOCKFAIL, result);
				mDBHelper.updateOption(updateData);
				
				item = new HashMap<String, String>();
				item.put("item1", "잠금해제 실패시 카메라 촬영");
				if (result) {
					item.put("item2", "사용함");
				} else {
					item.put("item2", "사용 안함");
				}
				list.set(LOCK_FAIL_INTENT, item);
				simpleAdapter.notifyDataSetChanged();
				break;
			case BACKUP_INTENT:
				result = data.getBooleanExtra("use", false);
				
				updateData.put(LocalDBAdapter.KEY_OPTION_USE_BACKUP, result);
				mDBHelper.updateOption(updateData);
				
				item = new HashMap<String, String>();
				item.put("item1", "주소록 백업 설정");
				if (result) {
					item.put("item2", "사용함");
				} else {
					item.put("item2", "사용 안함");
				}
				list.set(BACKUP_INTENT, item);
				simpleAdapter.notifyDataSetChanged();
				break;
			}
		}
	}

}
