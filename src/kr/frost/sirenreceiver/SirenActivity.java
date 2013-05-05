package kr.frost.sirenreceiver;

import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SirenActivity extends Activity {

	MediaPlayer mPlayer;
	AudioManager aManager;

	private int maxVol = 0;

	private TimerTask mTask;
	private Timer mTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_siren);
	
		mPlayer = new MediaPlayer();
		
		mPlayer.setLooping(true);
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mPlayer = MediaPlayer.create(SirenActivity.this, R.raw.siren);
		
		aManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		
		maxVol = aManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		aManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, AudioManager.FLAG_PLAY_SOUND);
		
		mPlayer.start();

		mTask = new TimerTask() {
			public void run() {
				mPlayer.stop();
			}
		};

		mTimer = new Timer();
		mTimer.schedule(mTask, 1000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.siren, menu);
		return true;
	}

}
