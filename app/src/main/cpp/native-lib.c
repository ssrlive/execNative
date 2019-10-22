#include <jni.h>
#include <string.h>

extern JNIEXPORT jstring JNICALL
Java_com_ssrlive_execnative_MainActivity_stringFromJNI(JNIEnv *env, jobject This) {
    // https://blog.csdn.net/u011068702/article/details/71425585
    return (*env)->NewStringUTF(env, "Hello from C++");
}
