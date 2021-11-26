//
// Created by ssrlive on 2019-10-21.
//

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>
#include <stdbool.h>
#include <unistd.h>

volatile bool exitFlag = true;

int main(int argc, const char *argv[]) {
    for (int index = 0; index < argc; ++index) {
        if ( strcmp(argv[index], "--deadloop") == 0) {
            exitFlag = false;
            break;
        }
    }

    printf("My Command! argc = %d\n", argc);
    do {
        if (exitFlag == false) {
            sleep(1); // real tasks
        }
    } while (exitFlag == false);
    exitFlag = true;
    return 0;
}

#if defined(__ANDROID__)

#include <jni.h>

// https://stackoverflow.com/questions/31996914/how-to-pass-a-arraylistpoint-for-a-jni-c-function

// private native int runAppNative(ArrayList<String> cmd);
// external fun runAppNative( cmd: ArrayList<String>): Int
JNIEXPORT jint JNICALL
Java_com_ssrlive_execnative_NativeWrapper_runAppNative(JNIEnv *env, jclass clazz, jobject cmd) {
    int result = -1;
    jclass alCls = NULL;
    do {
        alCls = (*env)->FindClass(env, "java/util/ArrayList");
        if (alCls == NULL) {
            break;
        }
        jmethodID alGetId = (*env)->GetMethodID(env, alCls, "get", "(I)Ljava/lang/Object;");
        jmethodID alSizeId = (*env)->GetMethodID(env, alCls, "size", "()I");
        if (alGetId == NULL || alSizeId == NULL) {
            break;
        }

        int arrayCount = (int) ((*env)->CallIntMethod(env, cmd, alSizeId));
        if (arrayCount <= 0) {
            break;
        }

        char ** argv = NULL;
        argv = (char **) calloc(arrayCount, sizeof(char*));
        if (argv == NULL) {
            break;
        }

        for (int index = 0; index < arrayCount; ++index) {
            jobject obj = (*env)->CallObjectMethod(env, cmd, alGetId, index);
            assert(obj);
            const char *cid = (*env)->GetStringUTFChars(env, obj, NULL);
            assert(cid);

            argv[index] = strdup(cid);
            assert(argv[index]);
            (*env)->DeleteLocalRef(env, obj);
        }

        result = main(arrayCount, argv);

        for (int index = 0; index < arrayCount; ++index) {
            free(argv[index]);
            argv[index] = NULL;
        }
        free(argv);
        argv = NULL;
    } while (false);

    if (alCls) {
        (*env)->DeleteLocalRef(env, alCls);
    }
    return result;
}

JNIEXPORT jint JNICALL
Java_com_ssrlive_execnative_NativeWrapper_stopAppNative(JNIEnv *env, jclass clazz) {
    exitFlag = true;
    return 0;
}

#endif
