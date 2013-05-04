package com.example.uidesign;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

public class SignupActivity extends Activity {
	CheckBox agreeCheck;
	DatePicker birthPicker;
	EditText passText;
	EditText mailText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

		agreeCheck = (CheckBox) findViewById(R.id.agreeCheck);
		birthPicker = (DatePicker) findViewById(R.id.birthPicker);
		passText = (EditText) findViewById(R.id.passText);
		mailText = (EditText) findViewById(R.id.mailText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.signup, menu);
		return true;
	}

}
