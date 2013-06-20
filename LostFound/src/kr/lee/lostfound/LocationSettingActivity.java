package kr.lee.lostfound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LocationSettingActivity extends Activity implements OnClickListener{
	Button locPrevBtn;
	Button locUseBtn;
	Button locUnuseBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locationsetting);
		
		locPrevBtn = (Button) findViewById(R.id.locationPreviewButton);
		locUseBtn = (Button) findViewById(R.id.locationUsingButton);
		locUnuseBtn = (Button) findViewById(R.id.locationUnusingButton);
		
		locPrevBtn.setOnClickListener(this);
		locUseBtn.setOnClickListener(this);
		locUnuseBtn.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_setting, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.locationPreviewButton:
			Intent intent = new Intent(LocationSettingActivity.this, LocationActivity.class);
			startActivity(intent);
			break;
		case R.id.locationUsingButton:
			Intent useIntent = new Intent();
			useIntent.putExtra("use", true);
			setResult(Activity.RESULT_OK, useIntent);
			finish();
			break;
		case R.id.locationUnusingButton:
			Intent unuseIntent = new Intent();
			unuseIntent.putExtra("use", false);
			setResult(Activity.RESULT_OK, unuseIntent);
			finish();
			break;
		}
		
	}

}
