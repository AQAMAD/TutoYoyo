package fr.aqamad.tutoyoyo.utils;

import android.os.Looper;

import fr.aqamad.tutoyoyo.BuildConfig;

/**
 * Created by Gregoire on 28/10/2015.
 */
public class ThreadPreconditions {
    public static void checkOnMainThread() {
        if (BuildConfig.DEBUG) {
            if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                throw new IllegalStateException("This method should be called from the Main Thread");
            }
        }
    }
}
