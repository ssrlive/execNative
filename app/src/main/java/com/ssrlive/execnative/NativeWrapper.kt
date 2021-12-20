package com.ssrlive.execnative

import java.util.ArrayList

object NativeWrapper {
    @JvmStatic
    external fun stringFromJNI(): String?
    @JvmStatic
    external fun runAppNative(cmd: ArrayList<String?>?): Int
    @JvmStatic
    external fun stopAppNative(): Int

    init {
        System.loadLibrary("mycommand2")
    }
}