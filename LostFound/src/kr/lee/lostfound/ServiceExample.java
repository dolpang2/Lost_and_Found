package kr.lee.lostfound;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class ServiceExample extends Service {
	boolean mQuit;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Toast.makeText(this, "Service End", Toast.LENGTH_SHORT).show();
		mQuit = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		mQuit = false;
		WorkingThread thread = new WorkingThread(this, mHandler);
		thread.start();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	class WorkingThread extends Thread {
		ServiceExample mParent;
		Handler mHandler;
		String[] arNews = { "What", "The", "Fuck?" };

		public WorkingThread(ServiceExample parent, Handler handler) {
			mParent = parent;
			mHandler = handler;
		}

		public void run() {
			for (int idx = 0; mQuit == false; idx++) {
				Message msg = new Message();
				msg.what = 0;
				msg.obj = arNews[idx % arNews.length];
				mHandler.sendMessage(msg);
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					;
				}
			}
		}
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				String news = (String) msg.obj;
				Toast.makeText(ServiceExample.this, news, Toast.LENGTH_SHORT).show();
			}
		}
	};
}
