#include <string.h>
#include <jni.h>

#include "StringParserWithJNI.h"

jint Java_kr_lee_lostfound_StringParserWithJNI_stringFromJNI(JNIEnv *env, jobject obj, jstring mms, jstring pass){
	const char *nativeString = (*env)->GetStringUTFChars(env, javaString, 0);

   // use your string

   (*env)->ReleaseStringUTFChars(env, javaString, nativeString);

}

