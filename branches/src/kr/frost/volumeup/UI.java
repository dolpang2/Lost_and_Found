/* 볼륨 조절 테스트 어플리케이션, 버튼으로 구현되어있지만 걍 함수선언만 하면 됨;;
 * 
 * 에뮬레이터에서는 동작이 잘 안됨...
 * ICS인 내 폰에서는 잘 만 됨...
 * ㅠㅠㅠㅠㅠㅠ
 * 2013/04/27 PM 3:17 by Frost, 근데 왜 내가 이시간에 이런짓을...?
 */

package kr.frost.volumeup;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class UI extends Activity {

	TextView text_vol;
	SeekBar bar_vol;
	Button btn_volmax;

	MediaPlayer mPlayer;
	AudioManager aManager;

	private int maxVol = 0;
	private int currVol = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui);

		text_vol = (TextView) findViewById(R.id.text_vol);
		bar_vol = (SeekBar) findViewById(R.id.bar_vol);
		btn_volmax = (Button) findViewById(R.id.btn_volmax);

		mPlayer = MediaPlayer.create(UI.this, R.raw.binding_of_isaac);
		aManager = (AudioManager) getSystemService(AUDIO_SERVICE);

		maxVol = aManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		currVol = aManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		bar_vol.setMax(maxVol);
		bar_vol.setProgress(currVol);
		
		text_vol.setText("Volume : " + currVol);

		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mPlayer.setLooping(true);
		mPlayer.start();

		bar_vol.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				aManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
				text_vol.setText("Volume : " + progress);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		btn_volmax.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				aManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, AudioManager.FLAG_PLAY_SOUND);
				bar_vol.setProgress(maxVol);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui, menu);
		return true;
	}

}
