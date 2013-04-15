package com.example.uidesign;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;

public class MainActivity extends Activity {
  public Button settingButton;
  public Button changeEmailButton;
  public Button changePassword;
  public Button supportButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    settingButton = (Button)findViewById(R.id.action_settings);
    changeEmailButton = (Button)findViewById(R.id.changeRegisteredEmail);
    changePassword = (Button)findViewById(R.id.changePassword);
    supportButton = (Button)findViewById(R.id.support);
    

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

}
