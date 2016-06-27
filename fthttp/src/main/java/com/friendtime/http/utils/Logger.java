package com.friendtime.http.utils;

import android.util.Log;

/**
 * Created by wutao on 2016/2/26.
 */
public class Logger {

    public static boolean isDebug = false;
    private static String prefixName = "";

    public static void setDebugMode(boolean debug) {
        isDebug = debug;
    }

    public static void setPrefixName(String prefixName) {
        Logger.prefixName = prefixName;
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(getLogTag(tag), msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(getLogTag(tag), msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(getLogTag(tag), msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(getLogTag(tag), msg);
        }
    }

    private static String getLogTag(String tag) {
        return String.format("%s[%s]", prefixName, tag);
    }
}
