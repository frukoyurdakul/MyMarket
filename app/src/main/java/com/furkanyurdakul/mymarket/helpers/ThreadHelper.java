package com.furkanyurdakul.mymarket.helpers;

import android.os.Handler;
import android.os.Looper;

/**
 * The helper class to switch between background and main threads.
 */
public class ThreadHelper
{
    public static void runOnBackgroundThread(Runnable runnable)
    {
        if (isOnMainThread())
            new Thread(runnable).start();
        else
            runnable.run();
    }

    public static void runOnMainThread(Runnable runnable)
    {
        if (isOnMainThread())
            runnable.run();
        else
            new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static boolean isOnMainThread()
    {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
