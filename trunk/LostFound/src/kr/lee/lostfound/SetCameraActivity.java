package kr.lee.lostfound;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SetCameraActivity extends Activity implements OnClickListener {
	Button camUseBtn;
	Button camUnuseBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setcamera);

		camUseBtn = (Button) findViewById(R.id.cameraUse);
		camUnuseBtn = (Button) findViewById(R.id.cameraUnuse);

		camUseBtn.setOnClickListener(this);
		camUnuseBtn.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_lock, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cameraUse:
			Intent useIntent = new Intent();
			useIntent.putExtra("use", true);
			setResult(Activity.RESULT_OK, useIntent);
			finish();
			break;
		case R.id.cameraUnuse:
			Intent unuseIntent = new Intent();
			unuseIntent.putExtra("use", false);
			setResult(Activity.RESULT_OK, unuseIntent);
			finish();
			break;
		}

	}

}
