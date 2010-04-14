#ifndef DACAPO_THREAD_H
#define DACAPO_THREAD_H

#include "dacapo.h"

void thread_init();
void thread_capabilities(const jvmtiCapabilities* availableCapabilities, jvmtiCapabilities* capabilities);
void thread_callbacks(const jvmtiCapabilities* capabilities, jvmtiEventCallbacks* callbacks);
void thread_logon(JNIEnv* env);

void thread_log(JNIEnv* env, jthread thread, jlong thread_tag, jboolean thread_has_new_tag);

void JNICALL callbackThreadStart(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread);
void JNICALL callbackThreadEnd(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread);
void JNICALL callbackMethodEntry(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jmethodID method);
void JNICALL callbackMethodExit(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jmethodID method, jboolean was_popped_by_exception, jvalue return_value);

#endif
