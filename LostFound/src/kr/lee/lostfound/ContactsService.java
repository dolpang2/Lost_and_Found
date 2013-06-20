package kr.lee.lostfound;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;

public class ContactsService extends Service {
	boolean mQuit;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Toast.makeText(this, "Service End", Toast.LENGTH_SHORT).show();
		mQuit = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		Toast.makeText(ContactsService.this, "백업 시작", Toast.LENGTH_SHORT).show();
		new BackupContactsTask().execute();

		mQuit = false;
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class BackupContactsTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected void onCancelled() {
			// Forced Cancel, 여기로 오면 설계상 문제 있는것임....
			Toast.makeText(ContactsService.this, "주소록 백업에 실패하였습니다.", Toast.LENGTH_SHORT).show();

			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == false) {
				Toast.makeText(ContactsService.this, "File Send Error", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ContactsService.this, "File Send Success?", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
		}

		protected void onProgressUpdate(Integer... values) {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			int contactsLength = 0;
			int iterator = 0;
			List<String[]> contactsList = new ArrayList<String[]>();
			ContentResolver cr = getContentResolver();

			Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

			int ididx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			int nameidx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

			Log.e("ididx", String.valueOf(ididx));
			Log.e("nameidx", String.valueOf(nameidx));

			cursor.moveToFirst();
			contactsLength = cursor.getCount();

			while (cursor.moveToNext()) {
				iterator++;
				publishProgress(iterator, contactsLength);

				String name = "";
				String mobile = "";
				String home = "";
				String work = "";

				name = cursor.getString(nameidx);

				String id = cursor.getString(ididx); // 사용자 ID

				Cursor cursor2 = cr
						.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
								new String[] { id }, null);

				int typeidx = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
				int numidx = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

				while (cursor2.moveToNext()) {
					String num = cursor2.getString(numidx);
					switch (cursor2.getInt(typeidx)) {
					case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
						mobile = num;
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
						home = num;
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
						work = num;
						break;
					}
				}
				cursor2.close();
				String[] contactString = { name, mobile, home, work };
				contactsList.add(contactString);
			}

			String sdcard_path = Environment.getExternalStorageDirectory().getAbsolutePath();
			String csv = sdcard_path + "/lostfound/contacts.csv";

			CSVWriter writer;
			try {
				writer = new CSVWriter(new FileWriter(csv));
				writer.writeAll(contactsList);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				return sendEmail("alchemist_d@naver.com", "주소록 백업 파일입니다.",
						"주소록이 정상적으로 백업되어, 첨부파일로 보내드립니다.\n나중에 앱을 재 설치하신 후, 주소록 복원 버튼을 통해 복원하실 수 있습니다.",
						new String[] { csv });
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	}

	public static boolean sendEmail(String to, String subject, String message, String[] attachements)
			throws Exception {
		Mail mail = new Mail();
		if (subject != null && subject.length() > 0) {
			mail.setSubject(subject);
		} else {
			mail.setSubject("Subject");
		}

		if (message != null && message.length() > 0) {
			mail.setBody(message);
		} else {
			mail.setBody("Message");
		}

		mail.setTo(new String[] { to });

		if (attachements != null) {
			for (String attachement : attachements) {
				mail.addAttachment(attachement);
			}
		}
		return mail.send();
	}
}
