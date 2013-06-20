package kr.lee.lostfound;

public class nativeJava {
    public native int resultCompareString(byte[] sms, byte[] password);

    static {
        System.loadLibrary("nativeJava");
    }
}
