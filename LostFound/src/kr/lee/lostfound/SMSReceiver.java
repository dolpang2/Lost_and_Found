package kr.lee.lostfound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

	private LocalDBAdapter mDBHelper;

	@SuppressWarnings("unused")
	// 임시로 unused warning을 막았습니다. 모든 경우를 다 사용할 경우 지워주세요
	@Override
	public void onReceive(Context context, Intent intent) {
		mDBHelper = new LocalDBAdapter(context);
		mDBHelper.open(); // DB Open

		Cursor memberCursor = mDBHelper.selectAllOption();
		String pass = memberCursor.getString(memberCursor
				.getColumnIndexOrThrow(LocalDBAdapter.KEY_MEMBER_PASS));

		memberCursor.close();

		Cursor optionCursor = mDBHelper.selectAllOption();

		boolean chkSiren = optionCursor.getInt(optionCursor
				.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_SIREN)) > 0;
		boolean chkGPS = optionCursor.getInt(optionCursor
				.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_GPS)) > 0;
		boolean chkCamera = optionCursor.getInt(optionCursor
				.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_CAMERA)) > 0;
		boolean chkLock = optionCursor.getInt(optionCursor
				.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_LOCK)) > 0;
		boolean chkLockFail = optionCursor.getInt(optionCursor
				.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_LOCKFAIL)) > 0;
		boolean chkBackup = optionCursor.getInt(optionCursor
				.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_BACKUP)) > 0;

		optionCursor.close();

		Bundle extras = intent.getExtras();

		if (extras != null) {
			Object[] pdus = (Object[]) extras.get("pdus");
			SmsMessage[] messages = new SmsMessage[pdus.length];

			for (int i = 0; i < pdus.length; i++) {
				messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
			}

			for (SmsMessage message : messages) {
				String msg = message.getMessageBody();
				if (msg.startsWith("@경보음 " + pass) && chkSiren) {
					Intent startActivity = new Intent();
					startActivity.setClass(context, SirenActivity.class);
					startActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					context.startActivity(startActivity);
				} else {
					Toast.makeText(context, "usual message", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

}
