
package kr.lee.lostfound;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

public class TestJNI extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_jni);

        nativeJava nj = new nativeJava();

        TextView tv = (TextView)findViewById(R.id.textView);
        int i = -1;
        String str;
        
        try {
            i = nj.resultCompareString("@위치1234".getBytes("KSC5601"),
                    "1234".getBytes("KSC5601"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = Integer.toString(i);

        tv.setText(str);
    }

}
