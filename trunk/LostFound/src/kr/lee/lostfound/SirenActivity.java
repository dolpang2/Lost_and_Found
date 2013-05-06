package kr.lee.lostfound;

import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class SirenActivity extends Activity implements TextToSpeech.OnInitListener {
	MediaPlayer mPlayer;
	AudioManager aManager;

	private int maxVol = 0;

	private TextToSpeech mTts;

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

		//mPlayer.start();

		mTts = new TextToSpeech(this, this);
		sayHello();
	}

	@Override
	protected void onDestroy() {
		mPlayer.stop();
		mPlayer.release();
		super.onDestroy();
	}

	public void onInit(int status) {
		// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
		if (status == TextToSpeech.SUCCESS) {
			// Set preferred language to US english.
			// Note that a language may not be available, and the result will indicate this.
			int result = mTts.setLanguage(Locale.US);
			// Try this someday for some interesting results.
			// int result mTts.setLanguage(Locale.FRANCE);
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Lanuage data is missing or the language is not supported.
				Log.e("TAG", "Language is not available.");
			} else {
				sayHello();
			}
		} else {
			// Initialization failed.
			Log.e("TAG", "Could not initialize TextToSpeech.");
		}
	}

	private static final Random RANDOM = new Random();
	private static final String[] HELLOS = { "Hello", "Salutations", "Greetings", "Howdy",
			"What's crack-a-lackin?", "That explains the stench!" };

	private void sayHello() {
		// Select a random hello.
		int helloLength = HELLOS.length;
		String hello = HELLOS[RANDOM.nextInt(helloLength)];
		mTts.speak(hello, TextToSpeech.QUEUE_FLUSH, // Drop all pending entries in the playback queue.
				null);
	}
}