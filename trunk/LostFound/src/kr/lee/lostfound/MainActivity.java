package kr.lee.lostfound;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	public Button settingButton;
	public Button changeEmailButton;
	public Button chagnePasswordButton;
	public Button supportButton;

	private LocalDBAdapter mDBHelper;

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
		super.onCreateOptionsMenu(menu);

		menu.add(0, 0, Menu.NONE, "Debug - Delete All Database").setIcon(android.R.drawable.ic_menu_add);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case 0:
			mDBHelper = new LocalDBAdapter(this);
			mDBHelper.open(); // DB Open
			mDBHelper.deleteAllMember(); // Local DB Reroll
			mDBHelper.deleteOption();  // Local DB Reroll

			Toast.makeText(this, "모든 로컬 데이터베이스가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
			Toast.makeText(this, "회원가입 테스트 용도로만 사용하세요!!", Toast.LENGTH_SHORT).show();
			finish();
			return true;
		}

		return false;
	}

}
