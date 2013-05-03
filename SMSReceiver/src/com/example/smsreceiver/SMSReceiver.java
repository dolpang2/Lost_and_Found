package com.example.smsreceiver;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
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
				// String from = message.getOriginatingAddress();
				if (msg.startsWith("@")) {
					Toast.makeText(context, "special opcode detected", Toast.LENGTH_SHORT).show();
					Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "usual message", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
