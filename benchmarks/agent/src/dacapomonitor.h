#ifndef DACAPO_MONITOR_H
#define DACAPO_MONITOR_H

#include "dacapo.h"

void monitor_init();
void monitor_capabilities(const jvmtiCapabilities* availableCapabilities, jvmtiCapabilities* capabilities);
void monitor_callbacks(const jvmtiCapabilities* capabilities, jvmtiEventCallbacks* callbacks);
void monitor_logon(JNIEnv* env);

void JNICALL callbackMonitorContendedEnter(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jobject object);
void JNICALL callbackMonitorContendedEntered(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jobject object);
void JNICALL callbackMonitorWait(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jobject object, jlong timeout);
void JNICALL callbackMonitorWaited(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jobject object, jboolean timed_out);

#endif
