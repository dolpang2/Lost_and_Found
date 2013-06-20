#include <string.h>
#include <jni.h>

#include "nativeJava.h"

const static int LENGTH_OF_PASSWORD = 4;
char* jbyteArray2cstr(JNIEnv* env, jbyteArray javaBytes);
jbyteArray cstr2jbyteArray(JNIEnv* env, const char* nativeStr);

char* jbyteArray2cstr(JNIEnv* env, jbyteArray javaBytes) {
  size_t len = (*env)->GetArrayLength(env, javaBytes);
  jbyte* nativeBytes = (*env)->GetByteArrayElements(env, javaBytes, 0);
  char* nativeStr = (char*)malloc(len + 1);

  strncpy(nativeStr, nativeBytes, len);
  nativeStr[len] = '\0';
  (*env)->ReleaseByteArrayElements(env, javaBytes, nativeBytes, JNI_ABORT);

  return nativeStr;
}

jbyteArray cstr2jbyteArray(JNIEnv* env, const char* nativeStr) {
  jbyteArray javaBytes;
  int len = strlen(nativeStr);
  javaBytes = (*env)->NewByteArray(env, len);
  (*env)->SetByteArrayRegion(env, javaBytes, 0, len, (jbyte*)nativeStr);

  return javaBytes;
}

jint Java_kr_lee_lostfound_nativeJava_resultCompareString
  (JNIEnv* env, jobject object, jbyteArray byteSms, jbyteArray bytePassword) {
    char* sms;
    char* password;
    int compareValue1;
    int compareValue2;
    
    sms = jbyteArray2cstr(env, byteSms);
    password = jbyteArray2cstr(env, bytePassword);


    char* shiftPointer1 = sms + 5;
    char* shiftPointer2 = sms + 6;
    compareValue1 = strncmp(shiftPointer1, password, LENGTH_OF_PASSWORD);
    compareValue2 = strncmp(shiftPointer2, password, LENGTH_OF_PASSWORD);

    if (compareValue1 == 0 || compareValue2 == 0) {
      return 1;
    }

    return 0;
}