//
// Created by test on 16/1/7.
//
#include "com_santiago_catbox_NDK_TestNDK.h"


JNIEXPORT jstring JNICALL Java_com_santiago_catbox_NDK_TestNDK_ndkReturnHelloWorld
        (JNIEnv * env, jclass obj){
    float a = 10.0;
   // float b = a / 0;
    int i =10;
    int j = 0;
    int k = i/j;
   // LOGE("#####   b = %d", i);
    return (*env)->NewStringUTF(env,"Hello World!   This just a test for Android Studio NDK JNI developer!");
}