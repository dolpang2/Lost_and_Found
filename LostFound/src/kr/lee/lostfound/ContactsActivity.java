package kr.lee.lostfound;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;

public class ContactsActivity extends Activity implements OnClickListener {
	static int REQUEST_LOAD = 1000;
	private Button btn_backup;
	private Button btn_restore;
	private Button btn_use;
	private Button btn_unuse;

	private ProgressDialog restoreDialog;
	private ProgressDialog backupDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);

		btn_backup = (Button) findViewById(R.id.backupContactsButton);
		btn_restore = (Button) findViewById(R.id.restoreContactsButton);
		btn_use = (Button) findViewById(R.id.useContacts);
		btn_unuse = (Button) findViewById(R.id.unuseContacts);

		btn_backup.setOnClickListener(this);
		btn_restore.setOnClickListener(this);
		btn_use.setOnClickListener(this);
		btn_unuse.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.backupContactsButton:
			new BackupContactsTask().execute();
			break;

		case R.id.restoreContactsButton:
			Intent intent = new Intent(getBaseContext(), FileDialog.class);
			intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath());
			intent.putExtra(FileDialog.CAN_SELECT_DIR, true);
			intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
			intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "csv" });

			startActivityForResult(intent, REQUEST_LOAD);
			break;

		case R.id.useContacts:
			Intent useIntent = new Intent();
			useIntent.putExtra("use", true);
			setResult(Activity.RESULT_OK, useIntent);
			finish();

			break;
		case R.id.unuseContacts:
			Intent unuseIntent = new Intent();
			unuseIntent.putExtra("use", false);
			setResult(Activity.RESULT_OK, unuseIntent);
			finish();

			break;
		}
	}

	public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			String selectedFilePath = data.getStringExtra(FileDialog.RESULT_PATH);
			Toast.makeText(ContactsActivity.this, selectedFilePath, Toast.LENGTH_SHORT).show();
			if (selectedFilePath.endsWith(".csv")) {
				new RestoreContactsTask().execute(selectedFilePath);
			} else {
				Toast.makeText(ContactsActivity.this, "선택한 파일은 CSV파일이 아닙니다.", Toast.LENGTH_SHORT).show();
			}

		} else if (resultCode == Activity.RESULT_CANCELED) {
			Toast.makeText(ContactsActivity.this, "파일이 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
		}

	}

	public void writeData(List<String[]> contactsList) {

	}

	private class RestoreContactsTask extends AsyncTask<String, Integer, Integer> {
		private int contactsLength = 0;

		@Override
		protected void onCancelled() {
			// Forced Cancel, 여기로 오면 설계상 문제 있는것임....
			Toast.makeText(ContactsActivity.this, "주소록 복원에 실패하였습니다.", Toast.LENGTH_SHORT).show();
			restoreDialog.dismiss();
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			Toast.makeText(ContactsActivity.this, "주소록 복원이 성공적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();
			restoreDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// Dialog Popup
			restoreDialog = new ProgressDialog(ContactsActivity.this);
			restoreDialog.setTitle("주소록 복원 중");
			restoreDialog.setMessage("잠시만 기다려 주세요...");
			restoreDialog.setCancelable(false);
			restoreDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			restoreDialog.setMax(0);
			restoreDialog.show();
		}

		protected void onProgressUpdate(Integer... values) {
			restoreDialog.setProgress(values[0]);
			restoreDialog.setMax(values[1]);
		}

		@Override
		protected Integer doInBackground(String... params) {
			String path = params[0];

			try {
				CSVReader reader = new CSVReader(new FileReader(path));
				int iterator = 0;
				String[] row;

				String displayName = "";
				String mobileNumber = "";
				String homeNumber = "";
				String workNumber = "";

				List<?> content = reader.readAll();

				contactsLength = content.size();

				for (Object object : content) {
					iterator++;
					publishProgress(iterator, contactsLength);
					row = (String[]) object;

					displayName = row[0];
					mobileNumber = row[1];
					homeNumber = row[2];
					workNumber = row[3];

					ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

					ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
							.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
							.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

					//------------------------------------------------------ Names
					if (displayName.compareTo("") != 0 || displayName != null) {
						ops.add(ContentProviderOperation
								.newInsert(ContactsContract.Data.CONTENT_URI)
								.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
								.withValue(ContactsContract.Data.MIMETYPE,
										ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
								.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
										displayName).build());
					}

					//------------------------------------------------------ Mobile Number                     
					if (mobileNumber.compareTo("") != 0 || mobileNumber != null) {
						ops.add(ContentProviderOperation
								.newInsert(ContactsContract.Data.CONTENT_URI)
								.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
								.withValue(ContactsContract.Data.MIMETYPE,
										ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
								.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber)
								.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
										ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
					}

					//------------------------------------------------------ Home Numbers
					if (homeNumber.compareTo("") != 0 || homeNumber != null) {
						ops.add(ContentProviderOperation
								.newInsert(ContactsContract.Data.CONTENT_URI)
								.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
								.withValue(ContactsContract.Data.MIMETYPE,
										ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
								.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, homeNumber)
								.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
										ContactsContract.CommonDataKinds.Phone.TYPE_HOME).build());
					}

					//------------------------------------------------------ Work Numbers
					if (workNumber.compareTo("") != 0 || workNumber != null) {
						ops.add(ContentProviderOperation
								.newInsert(ContactsContract.Data.CONTENT_URI)
								.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
								.withValue(ContactsContract.Data.MIMETYPE,
										ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
								.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, workNumber)
								.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
										ContactsContract.CommonDataKinds.Phone.TYPE_WORK).build());
					}

					// Asking the Contact provider to create a new contact                 
					try {
						getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(ContactsActivity.this, "Exception: " + e.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				}

				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return 0;
		}
	}

	private class BackupContactsTask extends AsyncTask<String, Integer, Integer> {
		private int contactsLength = 0;

		@Override
		protected void onCancelled() {
			// Forced Cancel, 여기로 오면 설계상 문제 있는것임....
			Toast.makeText(ContactsActivity.this, "주소록 백업에 실패하였습니다.", Toast.LENGTH_SHORT).show();
			backupDialog.dismiss();
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			Toast.makeText(ContactsActivity.this, "주소록 백업이 성공적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();
			Toast.makeText(ContactsActivity.this, "sdcard/lostfount/output.csv에 저장되었습니다.", Toast.LENGTH_SHORT)
					.show();
			backupDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// Dialog Popup
			backupDialog = new ProgressDialog(ContactsActivity.this);
			backupDialog.setTitle("주소록 복원 중");
			backupDialog.setMessage("잠시만 기다려 주세요...");
			backupDialog.setCancelable(false);
			backupDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			backupDialog.setMax(0);
			backupDialog.show();
		}

		protected void onProgressUpdate(Integer... values) {
			backupDialog.setProgress(values[0]);
			backupDialog.setMax(values[1]);
		}

		@Override
		protected Integer doInBackground(String... params) {
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
			String csv = sdcard_path + "/lostfound/output.csv";

			CSVWriter writer;
			try {
				writer = new CSVWriter(new FileWriter(csv));
				writer.writeAll(contactsList);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return 0;
		}
	}
}
