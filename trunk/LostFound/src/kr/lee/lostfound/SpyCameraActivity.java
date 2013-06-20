package kr.lee.lostfound;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;

public class SpyCameraActivity extends Activity implements SurfaceHolder.Callback {

	private LocalDBAdapter mDBHelper;
	String email;
	
	private SurfaceView sv;

	private SurfaceHolder sHolder;
	private Camera mCamera;
	private Parameters parameters;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spycamera);
		mDBHelper = new LocalDBAdapter(SpyCameraActivity.this);
		mDBHelper.open(); // DB Open
		
		Cursor memberCursor = mDBHelper.selectAllMember();
		email = memberCursor.getString(memberCursor
				.getColumnIndexOrThrow(LocalDBAdapter.KEY_MEMBER_MAIL));
		
		memberCursor.close();
		sv = (SurfaceView) findViewById(R.id.surfaceView);

		sHolder = sv.getHolder();

		sHolder.addCallback(this);

		sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		parameters = mCamera.getParameters();

		mCamera.setParameters(parameters);
		mCamera.startPreview();

		Camera.PictureCallback mCall = new Camera.PictureCallback() {
			@Override
			// 사진찍는곳!!
			public void onPictureTaken(byte[] data, Camera camera) {
				String filePath = Environment.getExternalStorageDirectory() + "/lostfound/image.bmp";
				File pictureFile = new File(filePath);

				try {
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();
					
					new EmailTask().execute(filePath);
					finish();
				} catch (FileNotFoundException e) {

				} catch (IOException e) {
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		mCamera.takePicture(null, null, mCall);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = openFrontFacingCamera();
		try {
			mCamera.setPreviewDisplay(holder);

		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
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
	
	private class EmailTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected void onPostExecute(Boolean result) {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				return sendEmail(email, "[Lost+Found] 전면 카메라 촬영 파일입니다",
						"잠금화면에서 잠금 해제에 실패했거나, 사용자의 요청에 의해서 전면 카메라로 촬영한 결과입니다.\n꼭 핸드폰 찾으세요 ^_^",
						new String[] { params[0] });
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public static boolean sendEmail(String to, String subject, String message, String[] attachements)
			throws Exception {
		Mail mail = new Mail();
		if (subject != null && subject.length() > 0) {
			mail.setSubject(subject);
		} else {
			mail.setSubject("Subject");
		}

		if (message != null && message.length() > 0) {
			mail.setBody(message);
		} else {
			mail.setBody("Message");
		}

		mail.setTo(new String[] { to });

		if (attachements != null) {
			for (String attachement : attachements) {
				mail.addAttachment(attachement);
			}
		}
		return mail.send();
	}
}