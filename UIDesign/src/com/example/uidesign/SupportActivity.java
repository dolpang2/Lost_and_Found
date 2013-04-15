package com.example.uidesign;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class SupportActivity extends Activity {
  public Button setWarningButton;
  public Button setScreenMessageButton;
  public Button settingFinishButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_support);
    
    setWarningButton = (Button)findViewById(R.id.setWarningMessage);
    setScreenMessageButton = (Button)findViewById(R.id.setLockScreenMessage);
    settingFinishButton = (Button)findViewById(R.id.settingFinished);
  }

}
