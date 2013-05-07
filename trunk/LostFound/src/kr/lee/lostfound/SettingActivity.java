package kr.lee.lostfound;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener {
	private CheckBox chkboxSiren;
	private CheckBox chkboxGPS;
	private CheckBox chkboxCamera;
	private CheckBox chkboxLock;
	private CheckBox chkboxLockFail;
	private CheckBox chkboxBackup;
	private Button btnSirenSetting;
	private Button btnLockSetting;
	private Button btnConfirm;

	private LocalDBAdapter mDBHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		chkboxSiren = (CheckBox) findViewById(R.id.sirenCheck);
		chkboxGPS = (CheckBox) findViewById(R.id.gpsCheck);
		chkboxCamera = (CheckBox) findViewById(R.id.smsShootCheck);
		chkboxLock = (CheckBox) findViewById(R.id.phoneLockCheck);
		chkboxLockFail = (CheckBox) findViewById(R.id.failShootCheck);
		chkboxBackup = (CheckBox) findViewById(R.id.contactCheck);
		btnSirenSetting = (Button) findViewById(R.id.setWarningMessage);
		btnLockSetting = (Button) findViewById(R.id.setLockMessage);
		btnConfirm = (Button) findViewById(R.id.setSaveSetting);

		btnSirenSetting.setOnClickListener(this);
		btnLockSetting.setOnClickListener(this);
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

		chkboxSiren.setChecked(chkSiren);
		chkboxGPS.setChecked(chkGPS);
		chkboxCamera.setChecked(chkCamera);
		chkboxLock.setChecked(chkLock);
		chkboxLockFail.setChecked(chkLockFail);
		chkboxBackup.setChecked(chkBackup);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.setWarningMessage:
			Intent setWarningIntent = new Intent(SettingActivity.this, SetSirenMessageActivity.class);
			startActivity(setWarningIntent);
			break;
		case R.id.setLockMessage:
			Toast.makeText(SettingActivity.this, "구현중입니다...", Toast.LENGTH_SHORT).show();
			break;
		case R.id.setSaveSetting:
			ContentValues updateData = new ContentValues();
			updateData.put(LocalDBAdapter.KEY_OPTION_USE_SIREN, chkboxSiren.isChecked());
			updateData.put(LocalDBAdapter.KEY_OPTION_USE_GPS, chkboxGPS.isChecked());
			updateData.put(LocalDBAdapter.KEY_OPTION_USE_CAMERA, chkboxCamera.isChecked());
			updateData.put(LocalDBAdapter.KEY_OPTION_USE_LOCK, chkboxLock.isChecked());
			updateData.put(LocalDBAdapter.KEY_OPTION_USE_LOCKFAIL, chkboxLockFail.isChecked());
			updateData.put(LocalDBAdapter.KEY_OPTION_USE_BACKUP, chkboxBackup.isChecked());

			mDBHelper.updateOption(updateData);
			finish();
			break;
		}
	}

}
