package kr.lee.lostfound;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;

public class StartupActivity extends Activity {
	private LocalDBAdapter mDBHelper;
	private TimerTask mTask;
	private Timer mTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);

		mDBHelper = new LocalDBAdapter(this);
		mDBHelper.open(); // DB Open

		mTask = new TimerTask() {
			public void run() {
				int alreadyExist = 0;

				Cursor localDBResult = mDBHelper.selectAllMember();
				alreadyExist = localDBResult.getCount();

				Intent mainIntent = new Intent(StartupActivity.this, MainActivity.class);
				Intent signupIntent = new Intent(StartupActivity.this, SignupActivity.class);
				switch (alreadyExist) {
				case 1:
					startActivity(mainIntent);
					finish();
					break;
				case 0:
					startActivity(signupIntent);
					finish();
					break;
				default:
					mDBHelper.deleteAllMember(); // Local DB Reroll
					Toast.makeText(StartupActivity.this, "Local DB가 손상되었습니다.", Toast.LENGTH_SHORT).show();
					Toast.makeText(StartupActivity.this, "Local DB를 초기화합니다.", Toast.LENGTH_SHORT).show();
					startActivity(signupIntent);
					finish();
					break;
				}
			}
		};

		mTimer = new Timer();
		mTimer.schedule(mTask, 1500); // 일부로 1.5초동안 작업 지연.. 로고를 보여주고 싶어용 헤헤 ^^
	}
}
