#include <string.h>
#include <jni.h>

#include "nativeJava.h"
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
(JNIEnv* env, jobject jobj, jbyteArray jsms, jbyteArray jpass) {
  char* sms = jbyteArray2cstr(env, jsms);
  char* pass = jbyteArray2cstr(env, jpass);
  int i, j;

  char* shift1 = sms + 5;
  char* shift2 = sms + 6;
  i = strncmp(shift1, pass, 4);
  j = strncmp(shift2, pass, 4);

  if (i == 0 || j == 0) {
    return 1;
  }

  return 0;

  return 1;
}