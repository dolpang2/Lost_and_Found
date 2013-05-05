package kr.lee.lostfound;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends Activity {
	private CheckBox agreeCheck;
	private DatePicker birthPicker;
	private EditText passText;
	private EditText mailText;
	private Button confirmButton;

	private LocalDBAdapter mDBHelper;

	private Builder dialogBuilder;
	private ProgressDialog signupDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int alreadyExist = 0;

		mDBHelper = new LocalDBAdapter(this);
		mDBHelper.open(); // DB Open
		
		Cursor localDBResult = mDBHelper.selectAllMember();
		alreadyExist = localDBResult.getCount();

		switch (alreadyExist) {
		case 1:
			Intent mainIntent = new Intent(SignupActivity.this, MainActivity.class);
			startActivity(mainIntent);
			finish();
			break;
		case 0:
			break;
		default:
			mDBHelper.deleteAllMember(); // 로컬 DB Reroll
			Toast.makeText(SignupActivity.this, "Local DB의 데이터가 손상되었습니다.", Toast.LENGTH_SHORT).show();
			Toast.makeText(SignupActivity.this, "Local DB를 초기화합니다.", Toast.LENGTH_SHORT).show();
			break;
		}

		setContentView(R.layout.activity_signup);

		dialogBuilder = new AlertDialog.Builder(SignupActivity.this);

		agreeCheck = (CheckBox) findViewById(R.id.agreeCheck);
		birthPicker = (DatePicker) findViewById(R.id.birthPicker);
		passText = (EditText) findViewById(R.id.passText);
		mailText = (EditText) findViewById(R.id.mailText);
		confirmButton = (Button) findViewById(R.id.confirmButton);

		confirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean agree = agreeCheck.isChecked();
				String pass = passText.getText().toString();
				String mail = mailText.getText().toString();
				if (!agree) {
					dialogBuilder.setTitle("가입 실패").setMessage("개인정보 이용에\n동의하셔야 합니다.")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setPositiveButton("확인", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();
				} else if (pass.length() != 4) {
					// it's not Length 4 or Not Positive Integer
					dialogBuilder.setTitle("가입 실패").setMessage("4자리의 숫자를 입력해주세요")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setPositiveButton("확인", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();
				} else if (!isEmailAddress(mail)) {
					// It's not Email Address...
					dialogBuilder.setTitle("가입 실패").setMessage("유효한 메일주소를\n입력해주세요.")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setPositiveButton("확인", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();
				} else {
					Calendar mCalendar = Calendar.getInstance();
					String birthString = null;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

					mCalendar.set(Calendar.YEAR, birthPicker.getYear());
					mCalendar.set(Calendar.MONTH, birthPicker.getMonth());
					mCalendar.set(Calendar.DAY_OF_MONTH, birthPicker.getDayOfMonth());

					birthString = sdf.format(mCalendar.getTime());
					new ExampleAsyncTask().execute(birthString, mail, pass);
				}
			}
		});
	}
	/*
	 * Unused Method - isPositiveInteger
	 * 주어진 String에 대해 그 String이 양수의 숫자인지 확인하는 함수.
	 * 그런데 확인해 보니 이미 EditText에서 양수의 숫자만 입력 가능한것으로 파악되서
	 * 사용 안하고 있음. 사용 안해서 생기는 Warning을 억지로 막고 있으므로,
	 * 사용할 경우 @SuppressWarnings를 헤제하셔도 됩니다.
	 * 
	 * 2013. 5. 5 이정우
	 */
	@SuppressWarnings("unused")
	private static boolean isPositiveInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		for (int i = 0; i < length; i++) {
			char c = str.charAt(i);
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isEmailAddress(String str) {
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher m = p.matcher(str);
		boolean matchFound = m.matches();

		return matchFound;
	}

	/* AsyncTask<테스크 실행시 받는 파라메터, 백그라운드 -> 핸들러 전송 파라메터, 백그라운드 리턴> */
	private class ExampleAsyncTask extends AsyncTask<String, Integer, Integer> {
		@Override
		protected void onCancelled() {
			// 여기로 빠지는 경우는 없을거임
			Toast.makeText(SignupActivity.this, "회원가입 진행이 강제로 취소되었습니다.", Toast.LENGTH_LONG).show();
			signupDialog.dismiss();
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			switch (result) {
			case 6:
				mDBHelper.deleteAllMember(); // 로컬 DB 초기화..
				dialogBuilder.setTitle("가입 실패").setMessage("로걸 DB 정보 저장에 실패하였습니다. 관리자에게 문의해주세요.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 5:
				mDBHelper.deleteAllMember(); // 로컬 DB Reroll
				dialogBuilder.setTitle("가입 실패").setMessage("웹서버와 통신에 실패하였습니다. 인터넷 연결상태를 확인해주세요.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 4:
				mDBHelper.deleteAllMember(); // 로컬 DB Reroll
				dialogBuilder.setTitle("가입 실패").setMessage("XML Parsing에 실패하였습니다. 관리자에게 문의해주세요.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 3:
				mDBHelper.deleteAllMember(); // 로컬 DB Reroll
				dialogBuilder.setTitle("가입 실패").setMessage("MySQL 접속에 실패하였습니다. 관리자에게 문의해주세요.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 2:
				mDBHelper.deleteAllMember(); // 로컬 DB Reroll
				dialogBuilder.setTitle("가입 실패").setMessage("MySQL 문법에 오류가 있습니다. 관리자에게 문의해주세요.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 1:
				mDBHelper.deleteAllMember(); // 로컬 DB Reroll
				dialogBuilder.setTitle("가입 실패").setMessage("메일 주소가 중복되었습니다. 다른 메일주소를 입력해주세요.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 0:
				Toast.makeText(SignupActivity.this, "가입이 정상적으로 처리되었습니다.", Toast.LENGTH_LONG).show();
				Intent mainIntent = new Intent(SignupActivity.this, MainActivity.class);
				signupDialog.dismiss();
				startActivity(mainIntent);
				finish();
				break;
			}
			signupDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// 실행 되기전 실행되는 메소드
			signupDialog = new ProgressDialog(SignupActivity.this);
			signupDialog.setTitle("회원가입 중");
			signupDialog.setMessage("회원가입 진행 중입니다..");
			signupDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			switch (values[0]) {
			case 6:
				signupDialog.setMessage("로걸 DB에 정보 저장 중....                 ");
				break;
			case 5:
				signupDialog.setMessage("웹서버에 정보 전달 중....                    ");
				break;
			case 4:
				signupDialog.setMessage("웹서버의 응답을 처리하는 중....");
				break;
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			// 백그라운드에서 실행할 메소드
			int result = 6; // if Background works great, result 0

			String birth = params[0];
			String mail = params[1];
			String pass = params[2];
			String url = "http://todestrieb.cafe24.com/Lee/SignupProcess.jsp";
			String responseData;
			int parseCode;

			try {
				publishProgress(result); // 로걸 DB에 정보 저장 중...
				mDBHelper.createMember(birth, mail, pass);
				result--;
			} catch (SQLException e) {
				Log.e("SignupActivity - LocalDBHelper", e.toString());
				return result; // 실패하면 6이 들어감
			}

			try {
				publishProgress(result); // 웹서버에 정보 전달 중...
				responseData = JSPCommunicator.sendMemberData(birth, mail, pass, url);
				result--;
			} catch (Exception e) {
				Log.e("SignupActivity - JSPComunicator", e.toString());
				return result; // 실패하면 5가 들어감
			}

			publishProgress(result); // 웹서버의 응답을 기다리는 중...
			Log.e("SignupActivity - ParseXML", responseData);
			try {
				parseCode = ParseXML(responseData);
				result--;
				switch (parseCode) {
				case -2:
					// MYSQL Access Denied
					return 3;
				case -1:
					// Insert Failed but not catched by SQLException
					return 2;
				case 1064:
					// Syntax Error
					return 2;
				case 1292:
					// Data Truncation Error(Input NULL...)
					return 2;
				case 1062:
					// E-Mail Address Duplicate!!!!!!!!!!!!!
					return 1;
				case 0:
					result = 0;
				}
			} catch (Exception e) {
				Log.e("SignupActivity - ParseXML", e.getMessage());
				return result; // XMLParse 실패, 4 반환.
			}

			publishProgress(result); // 웹 DB에 정보 저장 성공.
			return result;
		}
	}

	public static int ParseXML(String xml) throws Exception {
		int result;

		// XML Document 객체 생성
		InputSource is = new InputSource(new StringReader(xml));
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

		// xpath 생성
		XPath xpath = XPathFactory.newInstance().newXPath();

		// 값 출력
		result = Integer.parseInt(xpath.evaluate("//response/code", document, XPathConstants.STRING)
				.toString());
		Log.e("ParseXML", xpath.evaluate("//response/state", document, XPathConstants.STRING).toString());

		return result; // code값 출력.
	}
}