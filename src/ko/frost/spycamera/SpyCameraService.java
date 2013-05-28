package ko.frost.spycamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class SpyCameraService extends Service implements Runnable {
	Camera frontCam;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		Thread spyThread = new Thread(this);
		spyThread.start(); // Main thread가 아닌 다른 thread에서 spy가 동작

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	} // 원격 method 실행시...

	@Override
	public void run() {
		Log.e("Thread", "Thread Start??");
		frontCam = openFrontFacingCamera();
		if (frontCam != null) {
			frontCam.startPreview();
			frontCam.takePicture(null, null, mPicture);
		} else {
			Log.e("thread", "there is no front camera....");
		}
		Log.e("Thread", "Thread End!!");
	}

	private Camera openFrontFacingCamera() {
		int cameraCount = 0;
		Camera cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				try {
					cam = Camera.open(camIdx);
				} catch (RuntimeException e) {
					Log.e("SpyCameraService", "Camera failed to open: " + e.getLocalizedMessage());
				}
			}
		}
		return cam;
	}

	PictureCallback mPicture = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			File pictureFile = getOutputMediaFile();
			if (pictureFile == null) {
				return;
			}
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {

			} catch (IOException e) {
			}
		}
	};

	private static File getOutputMediaFile() {
		File mediaStorageDir = new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

		return mediaFile;
	}
}
