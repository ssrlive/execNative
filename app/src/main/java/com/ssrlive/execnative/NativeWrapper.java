package com.ssrlive.execnative;

import java.util.ArrayList;

public class NativeWrapper {
    static {
        System.loadLibrary("mycommand2");
    }

    public static native int runAppNative(ArrayList<String> cmd);
    public static native int stopAppNative();
}
