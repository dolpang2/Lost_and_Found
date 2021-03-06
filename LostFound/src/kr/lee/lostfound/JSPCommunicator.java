package kr.lee.lostfound;

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
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class JSPCommunicator {
	public static String sendMemberData(String birth, String mail, String pass, String url) throws Exception {

		HttpPost request = makeHttpPost(birth, mail, pass, url);

		HttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 5000); // Connection Timeout Limit 5s
		String result = "";
		try {
			ResponseHandler<String> reshandler = new BasicResponseHandler();
			result = client.execute(request, reshandler);
		} catch (Exception e) {
			e.printStackTrace();
			// Result not created, so return value is empty string
		}
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
