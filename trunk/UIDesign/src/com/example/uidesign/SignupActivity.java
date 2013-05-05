package com.example.uidesign;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
	CheckBox agreeCheck;
	DatePicker birthPicker;
	EditText passText;
	EditText mailText;
	Button confirmButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

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
					Builder dlg = new AlertDialog.Builder(SignupActivity.this);
					dlg.setTitle("���� ����").setMessage("�������� �̿뿡\n�����ϼž� �մϴ�.").setIcon(R.drawable.ic_launcher)
							.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();
				} else if (pass.length() != 4) {
					// it's not Length 4 or Not Positive Integer
					Builder dlg = new AlertDialog.Builder(SignupActivity.this);
					dlg.setTitle("���� ����").setMessage("4�ڸ��� ���ڸ� �Է����ּ���").setIcon(R.drawable.ic_launcher)
							.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();
				} else if (!isEmailAddress(mail)) {
					// It's not Email Address...
					Builder dlg = new AlertDialog.Builder(SignupActivity.this);
					dlg.setTitle("���� ����").setMessage("��ȿ�� �����ּҸ�\n�Է����ּ���.").setIcon(R.drawable.ic_launcher)
							.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();
				} else {
					Toast.makeText(SignupActivity.this, "Success!", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.signup, menu);
		return true;
	}

	/*
	 * Unused Method private Static Boolean isPositiveInteger(String str)
	 * �־��� String�� ���� �� String�� ����� �������� Ȯ���ϴ� �Լ�.
	 * �׷��� Ȯ���� ���� �̹� EditText����
	 * ����� ���ڸ� �Է� �����Ѱ����� �ľǵǼ� ��� ���ϰ� ����.
	 * ��� ���ؼ� ����� Warning�� ������ ���� �����Ƿ�, ����� ���
	 * @SuppressWarnings�� �����ϼŵ� �˴ϴ�.
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


}
