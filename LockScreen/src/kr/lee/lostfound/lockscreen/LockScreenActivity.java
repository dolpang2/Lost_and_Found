package kr.lee.lostfound.lockscreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class LockScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lock_screen, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.custom_dialog,(ViewGroup) findViewById(R.id.layout_root));

            AlertDialog.Builder aDialog = new AlertDialog.Builder(LockScreenActivity.this);
            aDialog.setTitle("비밀번호를 입력하세요!");
            aDialog.setView(layout);
            
            final EditText edit = (EditText)layout.findViewById(R.id.text);
            
            aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // 만약에 DB에 저장된 패스와드와 같으면 -> 1234를 DB의 저장값으로 변경
                    if (edit.getText().toString().equals("1234")) {
                        LockScreenActivity.this.finish();
                    } else {
                        Toast.makeText(LockScreenActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog ad = aDialog.create();
            ad.show();
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        return false;
    }



}
