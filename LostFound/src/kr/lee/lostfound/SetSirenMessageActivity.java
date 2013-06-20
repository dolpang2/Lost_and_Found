package kr.lee.lostfound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SetSirenMessageActivity extends Activity implements OnClickListener {
	private Button previewButton;
	private Button useButton;
	private Button unuseButton;
	private EditText numOfPhone1;
	private EditText numOfPhone2;
	private EditText numOfPhone3;

	private LocalDBAdapter mDBHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setsirenmessage);

		previewButton = (Button) findViewById(R.id.sirenPrevButton);
		useButton = (Button) findViewById(R.id.lockFailUse);
		unuseButton = (Button) findViewById(R.id.lockFailUnuse);

		numOfPhone1 = (EditText) findViewById(R.id.numOfPhone1);
		numOfPhone2 = (EditText) findViewById(R.id.numOfPhone2);
		numOfPhone3 = (EditText) findViewById(R.id.numOfPhone3);

		previewButton.setOnClickListener(this);
		useButton.setOnClickListener(this);
		unuseButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_siren_message, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.sirenPrevButton:
			updateSirenMessage();
			Intent previewIntent = new Intent(SetSirenMessageActivity.this, SirenActivity.class);
			startActivity(previewIntent);
			break;

		case R.id.lockFailUse:
			updateSirenMessage();
			Intent useIntent = new Intent();
			useIntent.putExtra("use", true);
			setResult(Activity.RESULT_OK, useIntent);
			finish();
			break;

		case R.id.lockFailUnuse:
			updateSirenMessage();
			Intent unuseIntent = new Intent();
			unuseIntent.putExtra("use", false);
			setResult(Activity.RESULT_OK, unuseIntent);
			finish();
			break;
		}
	}

	private void updateSirenMessage() {
		String telephoneNumber = numOfPhone1.getText().toString() + "-" + numOfPhone2.getText().toString()
				+ "-" + numOfPhone3.getText().toString();
		mDBHelper = new LocalDBAdapter(this);
		mDBHelper.open(); // DB Open

		mDBHelper.updateSirenMessage(telephoneNumber);
	}
}
