
package kr.lee.lostfound;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

public class TestJNI extends Activity {
    private String mSms;
    private String mPassword;
    private int mResultValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_jni);

        nativeJava nj = new nativeJava();

        TextView tv = (TextView)findViewById(R.id.textView);
        mResultValue = -1;
        mSms = "@위치 1234";
        mPassword = "1234";
        
        /**
         * Compare Sms code & local DB Password
         */
        try {
            mResultValue = nj.resultCompareString(mSms.getBytes("KSC5601"), 
                    mPassword.getBytes("KSC5601"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        /**
         * if printed value = -1, Error
         * else if printed value = 0, password does not match sms code
         * else if printed value = 1, password matches sms code
         */
        tv.setText("" + mResultValue);
    }
}
