package kr.lee.lostfound;

public class StringParserWithJNI {
	
	public native int stringFromJNI(String mms, String pass);
	
	static {
		System.loadLibrary("nativeJava");
	}
}
