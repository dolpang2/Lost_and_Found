package kr.lee.lostfound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();

		if (extras != null) {
			Object[] pdus = (Object[]) extras.get("pdus");
			SmsMessage[] messages = new SmsMessage[pdus.length];

			for (int i = 0; i < pdus.length; i++) {
				messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
			}

			for (SmsMessage message : messages) {
				String msg = message.getMessageBody();
				if (msg.startsWith("@경보음")) {
			        Intent startActivity = new Intent(); 
			        startActivity.setClass(context, SirenActivity.class); 
			        startActivity.setFlags( 
			                Intent.FLAG_ACTIVITY_NEW_TASK 
			                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
			        context.startActivity(startActivity); 
				} else {
					Toast.makeText(context, "usual message", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

}
