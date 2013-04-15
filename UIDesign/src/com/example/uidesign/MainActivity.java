package com.example.uidesign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
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
    
    findViewById(R.id.action_settings).setOnClickListener(mClickListener);
    findViewById(R.id.changeRegisteredEmail).setOnClickListener(mClickListener);
    findViewById(R.id.changePassword).setOnClickListener(mClickListener);
    findViewById(R.id.support).setOnClickListener(mClickListener);

  }

  Button.OnClickListener mClickListener = new View.OnClickListener() {

    @Override
    public void onClick(View v) {
      // TODO Auto-generated method stub

      switch (v.getId()) {
      case R.id.action_settings:
        settingButton.setOnClickListener(new Button.OnClickListener() {
          @Override
          public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent 
            = new Intent(MainActivity.this, SupportActivity.class);
            
            startActivity(intent);
          }
        });
        break;
      default:
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
