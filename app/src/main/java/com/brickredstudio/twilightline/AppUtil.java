package com.brickredstudio.twilightline;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Build;
import android.util.Log;
import java.lang.reflect.Method;
import java.io.FileOutputStream;
import java.io.InputStream;

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
                Log.e(App.TAG, String.format(
                    "get process name failed: %s", e.toString()));
                return "";
            }
        }
    }
    public static boolean copyAsset(
        String assetName, String destFileName)
    {
        return copyAsset(assetName, destFileName, 1024);
    }

    public static boolean copyAsset(
        String assetName, String destFileName, int buffSize)
    {
        InputStream input = null;
        FileOutputStream output = null;
        try {
            input = App.getContext().getAssets().open(assetName);
            output = new FileOutputStream(destFileName);

            final byte data[] = new byte[buffSize];
            for (;;) {
                int count = input.read(data, 0, buffSize);
                if (count < 0) {
                    break;
                }
                output.write(data, 0, count);
            }

        } catch (Exception e) {
            Log.e(App.TAG, String.format(
                "copy asset failed: %s", e.toString()));
            return false;

        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (Exception e) {
                }
            }
        }

        return true;
    }
}
