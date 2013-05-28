package kr.frost.contactshandling;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private Button btn_backup1;
	private Button btn_backup2;
	private Button btn_restore;
	private TextView txt_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btn_backup1 = (Button) findViewById(R.id.btn_backup1);
		btn_backup2 = (Button) findViewById(R.id.btn_backup2);
		btn_restore = (Button) findViewById(R.id.btn_restore);
		
		btn_backup1.setOnClickListener(this);
		btn_backup2.setOnClickListener(this);
		btn_restore.setOnClickListener(this);
		
		txt_result = (TextView) findViewById(R.id.txt_result);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_backup1:
			String result = getContactsCursor();
			txt_result.setText("");
			txt_result.setText(result);
			break;
		case R.id.btn_backup2:
			break;
		case R.id.btn_restore:
			break;
		}
	}

	private String getContactsCursor() {
		StringBuffer buf = null;
		Cursor contacts = null;
		long contactID = 0l;

		buf = new StringBuffer();
		// String sortOrder = Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		// Cursor contactCursor = managedQuery(uri, projection, selection, selectionArgs, sortOrder);
		contacts = getContentResolver().query(
				Contacts.CONTENT_URI,
				new String[] { Contacts._ID, Contacts.CONTACT_PRESENCE, Contacts.CONTACT_STATUS,
						Contacts.CONTACT_STATUS_ICON, Contacts.CONTACT_STATUS_LABEL,
						Contacts.CONTACT_STATUS_RES_PACKAGE, Contacts.CONTACT_STATUS_TIMESTAMP,
						Contacts.CUSTOM_RINGTONE, Contacts.DISPLAY_NAME, Contacts.HAS_PHONE_NUMBER,
						Contacts.IN_VISIBLE_GROUP, Contacts.LAST_TIME_CONTACTED, Contacts.LOOKUP_KEY,
						Contacts.PHOTO_ID, Contacts.SEND_TO_VOICEMAIL, Contacts.STARRED,
						Contacts.TIMES_CONTACTED }, null, null, null);

		contacts.moveToFirst();
		if (!contacts.isAfterLast()) {
			do {
				contactID = contacts.getLong(0);
				buf.append("--- Contacts Lists ---\n");
				for (int idx = 0; idx < contacts.getColumnCount(); idx++) {
					if (contacts.getString(idx) != null) {
						buf.append(contacts.getColumnName(idx).toUpperCase() + ": " + contacts.getString(idx)
								+ "\n");
					}
				}
				buf.append("\n");
			} while (contacts.moveToNext());
		}
		contacts.close();
		return buf.toString();
	}

}
