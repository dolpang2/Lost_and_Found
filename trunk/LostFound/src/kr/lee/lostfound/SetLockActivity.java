package kr.lee.lostfound;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SetLockActivity extends Activity implements OnClickListener {
	Button lockUseBtn;
	Button lockUnuseBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setlock);
		
		lockUseBtn = (Button) findViewById(R.id.lockUse);
		lockUnuseBtn = (Button) findViewById(R.id.lockUnuse);
		
		lockUseBtn.setOnClickListener(this);
		lockUnuseBtn.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_lock, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.lockUse:
			Intent useIntent = new Intent();
			useIntent.putExtra("use", true);
			setResult(Activity.RESULT_OK, useIntent);
			finish();
			break;
		case R.id.lockUnuse:
			Intent unuseIntent = new Intent();
			unuseIntent.putExtra("use", false);
			setResult(Activity.RESULT_OK, unuseIntent);
			finish();
			break;
		}
		
	}

}
