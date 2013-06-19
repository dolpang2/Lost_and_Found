package kr.frost.contactshandling;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;

public class MainActivity extends Activity implements OnClickListener {
	private static final String TAG = MainActivity.class.getName();
	private static final String FILENAME = "myFile.txt";

	static int REQUEST_SAVE = 1000;
	static int REQUEST_LOAD = 2000;
	private Button btn_backup1;
	private Button btn_backup2;
	private Button btn_restore;
	private TextView txt_result;
	String selectedFilePath = "";
	Boolean fileSelected = false;

	List<String[]> contactsList = new ArrayList<String[]>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txt_result = (TextView) findViewById(R.id.txt_result);
		btn_backup1 = (Button) findViewById(R.id.btn_backup1);
		btn_backup2 = (Button) findViewById(R.id.btn_backup2);
		btn_restore = (Button) findViewById(R.id.btn_restore);

		btn_backup1.setOnClickListener(this);
		btn_backup2.setOnClickListener(this);
		btn_restore.setOnClickListener(this);
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
		case R.id.btn_backup1:
			ContentResolver cr = getContentResolver();

			Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

			int ididx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			int nameidx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

			Log.e("ididx", String.valueOf(ididx));
			Log.e("nameidx", String.valueOf(nameidx));

			StringBuilder result = new StringBuilder();

			cursor.moveToFirst();
			while (cursor.moveToNext()) {
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
			//txt_result.setText(result);

			writeData(contactsList);
			break;

		case R.id.btn_backup2:
			Intent intent = new Intent(getBaseContext(), FileDialog.class);
			intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath());

			intent.putExtra(FileDialog.CAN_SELECT_DIR, true);

			intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);

			intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "csv" });

			startActivityForResult(intent, REQUEST_SAVE);
			break;

		case R.id.btn_restore:

			/*
			String DisplayName = "XYZ";
			String MobileNumber = "123456";
			String HomeNumber = "1111";
			String WorkNumber = "2222";

			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

			ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
					.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
					.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

			//------------------------------------------------------ Names
			if (DisplayName != null) {
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
						.withValue(ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, DisplayName)
						.build());
			}

			//------------------------------------------------------ Mobile Number                     
			if (MobileNumber != null) {
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
						.withValue(ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
						.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
								ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
			}

			//------------------------------------------------------ Home Numbers
			if (HomeNumber != null) {
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
						.withValue(ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
						.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
								ContactsContract.CommonDataKinds.Phone.TYPE_HOME).build());
			}

			//------------------------------------------------------ Work Numbers
			if (WorkNumber != null) {
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
						.withValue(ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
						.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
								ContactsContract.CommonDataKinds.Phone.TYPE_WORK).build());
			}

			// Asking the Contact provider to create a new contact                 
			try {
				getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(MainActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
			*/
			break;
		}
	}

	public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data) {

		if (resultCode == Activity.RESULT_OK) {

			if (requestCode == REQUEST_SAVE) {
				System.out.println("Saving...");
			} else if (requestCode == REQUEST_LOAD) {
				System.out.println("Loading...");
			}

			selectedFilePath = data.getStringExtra(FileDialog.RESULT_PATH);
			Toast.makeText(MainActivity.this, selectedFilePath, Toast.LENGTH_SHORT).show();
			if (selectedFilePath.endsWith("csv")) {
				Toast.makeText(MainActivity.this, "csv selected", Toast.LENGTH_SHORT).show();
				readData(selectedFilePath);
			} else {
				Toast.makeText(MainActivity.this, "Folder selected discard", Toast.LENGTH_SHORT).show();
			}

		} else if (resultCode == Activity.RESULT_CANCELED) {
			Toast.makeText(MainActivity.this, "File Not Selected", Toast.LENGTH_SHORT).show();
		}

	}

	public void writeData(List<String[]> contactsList) {
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
	}

	public void readData(String path) {
		try {
			CSVReader reader = new CSVReader(new FileReader(path));

			String[] row;

			List<?> content = reader.readAll();
			StringBuilder result = new StringBuilder();

			for (Object object : content) {
				row = (String[]) object;

				for (int i = 0; i < row.length; i++) {
					// display CSV values
					result.append("Cell column index: " + i + "\n");
					if(row[i].compareTo("") == 0){
						result.append("empty string" + "\n");
					}else{
						result.append(row[i] + "\n");
					}
						
					result.append("-------------" + "\n");
				}
			}
			reader.close();
			
			txt_result.setText(result);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
