package com.example.smsreceiver;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {
	public String NOTIFICATION_DATA = "NOTIFICATION_DATA";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* Notification Bulider는 API 11부터라 호환되는 library를 써야함... PAINFUL ㅡㅡ 
	public void createNotification(long when,String data){
		String notificationContent ="Notification Content Click Here to go more details";
		String notificationTitle ="Title";
		
		Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
		int smalIcon =R.drawable.ic_launcher;
		String notificationData="This is data : "+ data;

		Intent intent =new Intent(getApplicationContext(), NotificationDetailsActivity.class);
		intent.putExtra(NOTIFICATION_DATA, notificationData);

		intent.setData(Uri.parse("content://"+when));
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

		NotificationManager notificationManager =(NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE); 

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				getApplicationContext())
				.setWhen(when)
				.setContentText(notificationContent)
				.setContentTitle(notificationTitle)
				.setSmallIcon(smalIcon)
				.setAutoCancel(true)
				.setTicker(notificationTitle)
				.setLargeIcon(largeIcon)
				.setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_VIBRATE| Notification.DEFAULT_SOUND)
				.setContentIntent(pendingIntent);

		Notification notification=notificationBuilder.build();

		notificationManager.notify((int) when, notification);
	}
	*/
}
