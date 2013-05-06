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
			Toast.makeText(SignupActivity.this, "Local DB�� �����Ͱ� �ջ�Ǿ���ϴ�.", Toast.LENGTH_SHORT).show();
			Toast.makeText(SignupActivity.this, "Local DB�� �ʱ�ȭ�մϴ�.", Toast.LENGTH_SHORT).show();
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
					dialogBuilder.setTitle("���� ����").setMessage("�������� �̿뿡\n�����ϼž� �մϴ�.")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();
				} else if (pass.length() != 4) {
					// it's not Length 4 or Not Positive Integer
					dialogBuilder.setTitle("���� ����").setMessage("4�ڸ��� ���ڸ� �Է����ּ���")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();
				} else if (!isEmailAddress(mail)) {
					// It's not Email Address...
					dialogBuilder.setTitle("���� ����").setMessage("��ȿ�� �����ּҸ�\n�Է����ּ���.")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
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
	 * �־��� String�� ���� �� String�� ����� �������� Ȯ���ϴ� �Լ�.
	 * �׷��� Ȯ���� ���� �̹� EditText���� ����� ���ڸ� �Է� �����Ѱ����� �ľǵǼ�
	 * ��� ���ϰ� ����. ��� ���ؼ� ���� Warning�� ������ ���� �����Ƿ�,
	 * ����� ��� @SuppressWarnings�� �����ϼŵ� �˴ϴ�.
	 * 
	 * 2013. 5. 5 ������
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

	/* AsyncTask<�׽�ũ ����� �޴� �Ķ����, ��׶��� -> �ڵ鷯 ��� �Ķ����, ��׶��� ����> */
	private class ExampleAsyncTask extends AsyncTask<String, Integer, Integer> {
		@Override
		protected void onCancelled() {
			// ����� ������ ���� ��������
			Toast.makeText(SignupActivity.this, "ȸ���� ������ ������ ��ҵǾ���ϴ�.", Toast.LENGTH_LONG).show();
			signupDialog.dismiss();
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			switch (result) {
			case 6:
				mDBHelper.deleteAllMember(); // ���� DB �ʱ�ȭ..
				dialogBuilder.setTitle("���� ����").setMessage("�ΰ� DB ���� ���忡 �����Ͽ����ϴ�. ���ڿ��� �������ּ���.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 5:
				mDBHelper.deleteAllMember(); // ���� DB Reroll
				dialogBuilder.setTitle("���� ����").setMessage("�������� ��ſ� �����Ͽ����ϴ�. ���ͳ� ������¸� Ȯ�����ּ���.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 4:
				mDBHelper.deleteAllMember(); // ���� DB Reroll
				dialogBuilder.setTitle("���� ����").setMessage("XML Parsing�� �����Ͽ����ϴ�. ���ڿ��� �������ּ���.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 3:
				mDBHelper.deleteAllMember(); // ���� DB Reroll
				dialogBuilder.setTitle("���� ����").setMessage("MySQL ���ӿ� �����Ͽ����ϴ�. ���ڿ��� �������ּ���.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 2:
				mDBHelper.deleteAllMember(); // ���� DB Reroll
				dialogBuilder.setTitle("���� ����").setMessage("MySQL ���� ���� �ֽ��ϴ�. ���ڿ��� �������ּ���.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 1:
				mDBHelper.deleteAllMember(); // ���� DB Reroll
				dialogBuilder.setTitle("���� ����").setMessage("���� �ּҰ� �ߺ��Ǿ���ϴ�. �ٸ� �����ּҸ� �Է����ּ���.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				break;
			case 0:
				Toast.makeText(SignupActivity.this, "������ ���������� ó���Ǿ���ϴ�.", Toast.LENGTH_LONG).show();
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
			// ���� �Ǳ��� ����Ǵ� �޼ҵ�
			signupDialog = new ProgressDialog(SignupActivity.this);
			signupDialog.setTitle("ȸ���� ��");
			signupDialog.setMessage("ȸ���� ���� ���Դϴ�..");
			signupDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			switch (values[0]) {
			case 6:
				signupDialog.setMessage("�ΰ� DB�� ���� ���� ��....                 ");
				break;
			case 5:
				signupDialog.setMessage("�������� ���� ��� ��....                    ");
				break;
			case 4:
				signupDialog.setMessage("�������� ������ ó���ϴ� ��....");
				break;
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			// ��׶��忡�� ������ �޼ҵ�
			int result = 6; // if Background works great, result 0

			String birth = params[0];
			String mail = params[1];
			String pass = params[2];
			String url = "http://todestrieb.cafe24.com/Lee/SignupProcess.jsp";
			String responseData;
			int parseCode;

			try {
				publishProgress(result); // �ΰ� DB�� ���� ���� ��...
				mDBHelper.createMember(birth, mail, pass);
				result--;
			} catch (SQLException e) {
				Log.e("SignupActivity - LocalDBHelper", e.toString());
				return result; // �����ϸ� 6�� ��
			}

			try {
				publishProgress(result); // �������� ���� ��� ��...
				responseData = JSPCommunicator.sendMemberData(birth, mail, pass, url);
				result--;
			} catch (Exception e) {
				Log.e("SignupActivity - JSPComunicator", e.toString());
				return result; // �����ϸ� 5�� ��
			}

			publishProgress(result); // �������� ������ ��ٸ��� ��...
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
				return result; // XMLParse ����, 4 ��ȯ.
			}

			publishProgress(result); // �� DB�� ���� ���� ����.
			return result;
		}
	}

	public static int ParseXML(String xml) throws Exception {
		int result;

		// XML Document ��ü ��
		InputSource is = new InputSource(new StringReader(xml));
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

		// xpath ��
		XPath xpath = XPathFactory.newInstance().newXPath();

		// �� ���
		result = Integer.parseInt(xpath.evaluate("//response/code", document, XPathConstants.STRING)
				.toString());
		Log.e("ParseXML", xpath.evaluate("//response/state", document, XPathConstants.STRING).toString());

		return result; // code�� ���.
	}
}