package kr.lee.lostfound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

public class BootcompleteReceiver extends BroadcastReceiver {
	private LocalDBAdapter mDBHelper;
	Boolean isLocked = false;
	String pass = "";
	Boolean chkLockFail = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			mDBHelper = new LocalDBAdapter(context);
			mDBHelper.open();
			Boolean isLocked = mDBHelper.getIsLocked();

			Cursor memberCursor = mDBHelper.selectAllMember();
			String pass = memberCursor.getString(memberCursor
					.getColumnIndexOrThrow(LocalDBAdapter.KEY_MEMBER_PASS));

			memberCursor.close();
			Cursor optionCursor = mDBHelper.selectAllOption();
			Boolean chkLockFail = optionCursor.getInt(optionCursor
					.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_USE_LOCKFAIL)) > 0;

			optionCursor.close();


			if (isLocked) {
				Intent startActivity = new Intent();
				startActivity.setClass(context, LockScreenActivity.class);
				startActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				startActivity.putExtra("isLockFail", chkLockFail); // Boolean
				startActivity.putExtra("pass", pass); // String
				context.startActivity(startActivity);
			}

		}

	}
}