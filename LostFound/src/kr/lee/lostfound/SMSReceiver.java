package kr.lee.lostfound;

import java.io.UnsupportedEncodingException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

// 임시로 boolean 변수에 대한 unused warning을 막았습니다. 모든 경우를 다 사용할 경우 지워주세요.
@SuppressWarnings("unused")
public class SMSReceiver extends BroadcastReceiver {

	private LocalDBAdapter mDBHelper;
	private String pass;
	private int alreadyExist = 0;
	private boolean chkSiren = false;
	private boolean chkGPS = false;
	private boolean chkCamera = false;
	private boolean chkLock = false;
	private boolean chkLockFail = false;
	private boolean chkBackup = false;

	@Override
	public void onReceive(Context context, Intent intent) {

		mDBHelper = new LocalDBAdapter(context);
		try {
			mDBHelper.open(); // DB Open
			Cursor memberCursor = mDBHelper.selectAllMember();

			alreadyExist = memberCursor.getCount();
			if (alreadyExist == 1) {
				pass = memberCursor.getString(memberCursor
						.getColumnIndexOrThrow(LocalDBAdapter.KEY_MEMBER_PASS));

				memberCursor.close();

				Cursor optionCursor = mDBHelper.selectAllOption();

				chkSiren = optionCursor.getInt(optionCursor
						.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_SIREN)) > 0;
				chkGPS = optionCursor.getInt(optionCursor
						.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_GPS)) > 0;
				chkCamera = optionCursor.getInt(optionCursor
						.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_CAMERA)) > 0;
				chkLock = optionCursor.getInt(optionCursor
						.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_LOCK)) > 0;
				chkLockFail = optionCursor.getInt(optionCursor
						.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_LOCKFAIL)) > 0;
				chkBackup = optionCursor.getInt(optionCursor
						.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_BACKUP)) > 0;

				optionCursor.close();
			} else if (alreadyExist > 1) {
				mDBHelper.deleteAllMember(); // Local DB Reroll
				alreadyExist = 0;
				Toast.makeText(context, "Lost+Found : Local DB가 손상되었습니다.", Toast.LENGTH_SHORT).show();
				Toast.makeText(context, "Lost+Found : Local DB를 초기화합니다.", Toast.LENGTH_SHORT).show();
			}
		} catch (SQLException e) {
			Toast.makeText(context, "Lost+Found : 문자열을 인식하는데 문제가 있습니다.", Toast.LENGTH_SHORT).show();
		}

		if (alreadyExist == 1) {
			Bundle extras = intent.getExtras();

			if (extras != null) {
				Object[] pdus = (Object[]) extras.get("pdus");
				SmsMessage[] messages = new SmsMessage[pdus.length];

				for (int i = 0; i < pdus.length; i++) {
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}

				for (SmsMessage message : messages) {
					String msg = message.getMessageBody();
					String from = message.getOriginatingAddress();
					if (msg.startsWith("@경보음")) {
						if ((msg.startsWith("@경보음 " + pass) || msg.startsWith("@경보음" + pass)) && chkSiren) {
							Intent startActivity = new Intent();
							startActivity.setClass(context, SirenActivity.class);
							startActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
							context.startActivity(startActivity);
						}
						abortBroadcast();
					} else if (msg.startsWith("@주소록")) {
						if ((msg.startsWith("@주소록 " + pass) || msg.startsWith("@주소록" + pass)) && chkBackup) {
							Intent stService = new Intent("kr.lee.lostfound.ContactsService");
							context.startService(stService);
						}
						abortBroadcast();
					} else if (msg.startsWith("@위치")) {
						int result = 0;
						try {

							nativeJava nj = new nativeJava(); // JNI!
							result = nj
									.resultCompareString(msg.getBytes("KSC5601"), pass.getBytes("KSC5601")); // 결과값이 1이면 조건에 만족함
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						if ((result == 1) && chkGPS) {
							Intent stService = new Intent("kr.lee.lostfound.LocationService");
							stService.putExtra("address", from);
							context.startService(stService);
						}
						abortBroadcast();
					} else if (msg.startsWith("@잠금")) {
						int result = 0;
						try {

							nativeJava nj = new nativeJava(); // JNI!
							result = nj
									.resultCompareString(msg.getBytes("KSC5601"), pass.getBytes("KSC5601")); // 결과값이 1이면 조건에 만족함
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						if ((result == 1) && chkLock) {
							mDBHelper.setIsLocked(true);
							Intent startActivity = new Intent();
							startActivity.setClass(context, LockScreenActivity.class);
							startActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
							startActivity.putExtra("isLockFail", chkLockFail); // Boolean
							startActivity.putExtra("pass", pass); // String
							context.startActivity(startActivity);
						}
						abortBroadcast();
					} else if (msg.startsWith("@사진")) {
						int result = 0;
						try {

							nativeJava nj = new nativeJava(); // JNI!
							Toast.makeText(context, result +"aaa", 0).show();
							result = nj
									.resultCompareString(msg.getBytes("KSC5601"), pass.getBytes("KSC5601")); // 결과값이 1이면 조건에 만족함
							Toast.makeText(context, result +"bbb", 0).show();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						if ((result == 1) && chkCamera) {
							Intent startActivity = new Intent();
							startActivity.setClass(context, SpyCameraActivity.class);
							startActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
							context.startActivity(startActivity);
						}
						abortBroadcast();
					}
				}
			}
		}
	}
}
