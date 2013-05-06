package kr.lee.lostfound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	public Button settingButton;
	public Button changeEmailButton;
	public Button chagnePasswordButton;
	public Button supportButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		settingButton = (Button) findViewById(R.id.settingButton);
		changeEmailButton = (Button) findViewById(R.id.changeEmailButton);
		chagnePasswordButton = (Button) findViewById(R.id.changePasswordButton);
		supportButton = (Button) findViewById(R.id.supportButton);

		findViewById(R.id.settingButton).setOnClickListener(mClickListener);
		findViewById(R.id.changeEmailButton).setOnClickListener(mClickListener);
		findViewById(R.id.changePasswordButton).setOnClickListener(mClickListener);
		findViewById(R.id.supportButton).setOnClickListener(mClickListener);
	}

	Button.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.settingButton:
				Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
				startActivity(settingIntent);
				break;
			case R.id.changeEmailButton:
				Intent emailIntent = new Intent(MainActivity.this, EmailChangeActivity.class);
				startActivity(emailIntent);
				break;
			case R.id.changePasswordButton:
				Intent passIntent = new Intent(MainActivity.this, PasswordChangeActivity.class);
				startActivity(passIntent);
				break;
			case R.id.supportButton:
				Intent supportIntent = new Intent(MainActivity.this, SupportActivity.class);
				startActivity(supportIntent);
				break;
			}

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
