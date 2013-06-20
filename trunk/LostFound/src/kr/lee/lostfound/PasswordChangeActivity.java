package kr.lee.lostfound;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordChangeActivity extends Activity {
	EditText oldpass;
	EditText newpass;
	Button changeBtn;
	String pass;
	String mail;

	private LocalDBAdapter mDBHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changepassword);

		mDBHelper = new LocalDBAdapter(this);
		mDBHelper.open(); // DB Open
		Cursor memberCursor = mDBHelper.selectAllMember();

		pass = memberCursor.getString(memberCursor.getColumnIndexOrThrow(LocalDBAdapter.KEY_MEMBER_PASS));
		mail = memberCursor.getString(memberCursor.getColumnIndexOrThrow(LocalDBAdapter.KEY_MEMBER_MAIL));

		memberCursor.close();
		oldpass = (EditText) findViewById(R.id.oldpassword);
		newpass = (EditText) findViewById(R.id.newpassword);
		changeBtn = (Button) findViewById(R.id.finishChange);

		changeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (oldpass.getText().toString().compareTo(pass) == 0) {
					new PassChangeTask().execute(newpass.getText().toString());
				} else {
					Toast.makeText(PasswordChangeActivity.this, "비밀번호가 올바르지 않습니다", 0).show();

				}

			}
		});
	}

	private class PassChangeTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected void onPostExecute(Boolean result) {
		}

		@Override
		protected Boolean doInBackground(String... params) {

			String url = "http://todestrieb.cafe24.com/Lee/ChangePassword.jsp";
			String birth = "2013-06-21";
			String responseData = "";
			try {
				responseData = JSPCommunicator.sendMemberData(birth, mail, params[0], url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mDBHelper.setPassword(params[0]);
			Log.e("aaa", responseData);
			return true;
		}
	}

}
