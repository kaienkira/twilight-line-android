package com.brickredstudio.twilightline;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Build;
import java.lang.reflect.Method;

public final class AppUtil
{
    public static String getProcessName()
    {
        if (Build.VERSION.SDK_INT >= 28) {
            return Application.getProcessName();
        } else {
            try {
                @SuppressLint("PrivateApi")
                Class<?> classRef = Class.forName("android.app.ActivityThread");
                @SuppressLint("DiscouragedPrivateApi")
                Method methodRef = classRef.getDeclaredMethod("currentProcessName");
                return (String)methodRef.invoke(null);

            } catch (Exception e) {
                return "";
            }
        }
    }
}
