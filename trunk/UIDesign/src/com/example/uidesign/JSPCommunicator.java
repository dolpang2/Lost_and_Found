package com.example.uidesign;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/* 지금은 회원가입용 폼만 사용할 수 있도록 전용화 되어 있음
 * 이걸 범용으로 구현하려면 데이터를 Arraylist나 다른 자료구조형을 사용하여
 * 인자를 받아 적절히 처리하게 하면 될 것임
 * 
 * 일단 시간이 없어서 대충...
 * 2013. 5. 5 이정우
 */
public class JSPCommunicator {
	public static String sendMemberData(String birth, String mail, String pass, String url) throws Exception {

		HttpPost request = makeHttpPost(birth, mail, pass, url);

		HttpClient client = new DefaultHttpClient();
		ResponseHandler<String> reshandler = new BasicResponseHandler();
		String result = client.execute(request, reshandler);

		return result;
	}

	private static HttpPost makeHttpPost(String birth, String mail, String pass, String url) throws Exception {
		HttpPost request = new HttpPost(url);

		Vector<BasicNameValuePair> nameValue = new Vector<BasicNameValuePair>();
		nameValue.add(new BasicNameValuePair("birth", birth));
		nameValue.add(new BasicNameValuePair("mail", mail));
		nameValue.add(new BasicNameValuePair("pass", pass));

		request.setEntity(makeEntity(nameValue));

		return request;
	}

	private static HttpEntity makeEntity(Vector<BasicNameValuePair> nameValue) throws Exception {
		HttpEntity result = null;
		try {
			result = new UrlEncodedFormEntity(nameValue, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
}
