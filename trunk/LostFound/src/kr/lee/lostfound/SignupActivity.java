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

		Log.e("Test", "Change Character Set");
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
			mDBHelper.deleteAllMember(); // ���� DB Reroll
			Toast.makeText(SignupActivity.this, "Local DB가 손상되었습니다.", Toast.LENGTH_SHORT).show();
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
					dialogBuilder.setTitle("가입 실패").setMessage("개인정보 이용 약관에 동의하셔야 합니다.")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setPositiveButton("확인", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();
				} else if (pass.length() != 4) {
					// it's not Length 4 or Not Positive Integer
					dialogBuilder.setTitle("가입 실패").setMessage("비밀번호로 4자리의 숫자를 입력해주세요.")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setPositiveButton("확인", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();
				} else if (!isEmailAddress(mail)) {
					// It's not Email Address...
					dialogBuilder.setTitle("가입 실패").setMessage("유효한 이메일 주소를 입력해주세요.")
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
	 * 주어진 String이 양수 정수인지 확인하여 boolean반환
	 * 이미 입력폼에서 양수 정수의 4자리를 확인하므로 필요없어서
	 * 사용 안하고 있음.
	 * 사용 안하는 경우 뜨는 Warning을 Suppress하고 있으므로
	 * 만일 사용하게 된다면 SuppressWarnings를 제거해주시면 됩니다
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

	/* AsyncTask로 Progress Dialog를 구현하고 있습니다. 2013. 5. 5 이정우 */
	private class ExampleAsyncTask extends AsyncTask<String, Integer, Integer> {
		@Override
		protected void onCancelled() {
			// Forced Cancel, 여기로 오면 설계상 문제 있는것임..
			mDBHelper.deleteAllMember(); // Local DB Delete
			Toast.makeText(SignupActivity.this, "DB", Toast.LENGTH_LONG).show();
			signupDialog.dismiss();
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			switch (result) {
			case 6:
				mDBHelper.deleteAllMember(); // Local DB Delete
				dialogBuilder.setTitle("가입 실패")
						.setMessage("로컬 DB 저장에 실패하였습니다. 개발자에게 문의바랍니다.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 5:
				mDBHelper.deleteAllMember(); // Local DB Delete
				dialogBuilder.setTitle("가입 실패")
						.setMessage("웹 서버와 통신에 실패하였습니다. 인터넷 연결상태를 확인해주세요.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 4:
				mDBHelper.deleteAllMember(); // Local DB Delete
				dialogBuilder.setTitle("가입 실패")
						.setMessage("XML Parsing에 실패하였습니다. 개발자에게 문의바랍니다.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 3:
				mDBHelper.deleteAllMember(); // Local DB Delete
				dialogBuilder.setTitle("가입 실패")
						.setMessage("웹 DB 접근이 거부되었습니다. 개발자에게 문의바랍니다.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 2:
				mDBHelper.deleteAllMember(); // Local DB Delete
				dialogBuilder.setTitle("가입 실패")
						.setMessage("웹 DB Query문에 문법 오류가 있습니다. 개발자에게 문의바랍니다.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 1:
				mDBHelper.deleteAllMember(); // Local DB Delete
				dialogBuilder.setTitle("가입 실패")
						.setMessage("이미 가입된 메일주소입니다. 다른 메일 주소를 입력해주세요.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 0:
				Toast.makeText(SignupActivity.this, "회원 가입이 정상적으로 처리되었습니다.", Toast.LENGTH_LONG)
						.show();
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
			// Dialog Popup
			signupDialog = new ProgressDialog(SignupActivity.this);
			signupDialog.setTitle("가입 진행 중..");
			signupDialog.setMessage("잠시만 기다려 주세요....              ");
			signupDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			switch (values[0]) {
			case 6:
				signupDialog.setMessage("로컬 DB에 저장 중....                    ");
				break;
			case 5:
				signupDialog.setMessage("웹 서버에 요청을 전달하는 중....");
				break;
			case 4:
				signupDialog.setMessage("웹 서버의 응답을 처리하는 중....");
				break;
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			// Background Progress
			int result = 6; // if Background works great, result 0

			String birth = params[0];
			String mail = params[1];
			String pass = params[2];
			String url = "http://todestrieb.cafe24.com/Lee/SignupProcess.jsp";
			String responseData;
			int parseCode;

			try {
				publishProgress(result); // Local DB insert
				mDBHelper.createMember(birth, mail, pass);
				result--;
			} catch (SQLException e) {
				Log.e("SignupActivity - LocalDBHelper", e.toString());
				return result; // return 6 if failed
			}

			try {
				publishProgress(result); // Communicate with Web Server
				responseData = JSPCommunicator.sendMemberData(birth, mail, pass, url);
				result--;
			} catch (Exception e) {
				Log.e("SignupActivity - JSPComunicator", e.toString());
				return result; // return 5 if failed
			}

			publishProgress(result); // Parse Result Data
			Log.e("SignupActivity - ParseXML", responseData);
			try {
				parseCode = ParseXML(responseData);
				result--;
				switch (parseCode) {
				case -2:
					// MYSQL Access Denied
					result = 3;
				case -1:
					// Insert Failed but not catched by SQLException
					result = 2;
				case 1064:
					// Syntax Error
					result = 2;
				case 1292:
					// Data Truncation Error(Input NULL...)
					result = 2;
				case 1062:
					// E-Mail Address Duplicate!!!!!!!!!!!!!
					result = 1;
				case 0:
					result = 0;
				}
			} catch (Exception e) {
				Log.e("SignupActivity - ParseXML", e.getMessage());
				return result; // return 4 if failed ParseXML
			}
			publishProgress(result); // Parse Result Return (3,2,1 is Error, 0 is OK)
			return result;
		}
	}

	public static int ParseXML(String xml) throws Exception {
		int result;

		// XML Document Make
		InputSource is = new InputSource(new StringReader(xml));
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

		// XPath Make
		XPath xpath = XPathFactory.newInstance().newXPath();

		// Parse XML by XPath
		result = Integer.parseInt(xpath.evaluate("//response/code", document, XPathConstants.STRING)
				.toString());
		Log.e("ParseXML", xpath.evaluate("//response/state", document, XPathConstants.STRING).toString());

		return result; // Return Parse Result
	}
}