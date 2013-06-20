LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE	:= nativeJava
LOCAL_SRC_FILES	:= nativeJava.c

include $(BUILD_SHARED_LIBRARY)