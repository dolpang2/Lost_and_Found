package kr.lee.lostfound;

import java.util.Locale;
import android.app.Activity;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

public class SirenActivity extends Activity implements TextToSpeech.OnInitListener {
	private MediaPlayer mPlayer;
	private AudioManager aManager;

	private int maxVol = 0;

	private TextToSpeech mTts;

	private LocalDBAdapter mDBHelper;
	private Cursor resultCursor;
	private String message;

	private TextView phoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_siren);

		phoneNumber = (TextView) findViewById(R.id.sirenPhoneText);

		mDBHelper = new LocalDBAdapter(this);
		mDBHelper.open(); // DB Open

		resultCursor = mDBHelper.selectSirenMessage();
		startManagingCursor(resultCursor);
		message = resultCursor.getString(resultCursor
				.getColumnIndexOrThrow(LocalDBAdapter.KEY_OPTION_TTS_MESSAGE));
		resultCursor.close();
		phoneNumber.setText(message);

		mPlayer = new MediaPlayer();

		mPlayer.setLooping(true);
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mPlayer = MediaPlayer.create(SirenActivity.this, R.raw.siren);

		aManager = (AudioManager) getSystemService(AUDIO_SERVICE);

		maxVol = aManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		aManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, AudioManager.FLAG_PLAY_SOUND);

		//mPlayer.start();

		mTts = new TextToSpeech(this, this);
		//sayHello();
	}

	@Override
	protected void onDestroy() {
		mPlayer.stop();
		mPlayer.release();
		super.onDestroy();
	}

	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = mTts.setLanguage(Locale.US);
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTSMessage", "Language is not available.");
			} else {
				sayHello();
			}
		} else {
			Log.e("TTSMessage", "Could not initialize TextToSpeech.");
		}
	}

	private void sayHello() {
		mDBHelper.selectSirenMessage();
		String fixedMessage = "This phone is a lost phone. please call us at ";
		mTts.setSpeechRate((float) 1);
		mTts.speak(fixedMessage, TextToSpeech.QUEUE_FLUSH, // Drop all pending entries in the playback queue.
				null);
		mTts.setSpeechRate((float) 0.7);
		mTts.speak(message, TextToSpeech.QUEUE_ADD, null);
	}
}