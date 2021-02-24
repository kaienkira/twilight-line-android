package com.brickredstudio.twilightline;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.Log;
import java.lang.reflect.Method;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Scanner;
import androidx.appcompat.content.res.AppCompatResources;

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

    public static ColorStateList getColorStateList(int id)
    {
        return AppCompatResources.getColorStateList(App.getContext(), id);
    }

    public static String[] listAssets(String assetDirName)
    {
        try {
            return App.getContext().getAssets().list(assetDirName);

        } catch (Exception e) {
            Log.e(App.TAG, String.format(
                "list assets failed: %s", e.toString()));
            return null;
        }
    }

    public static boolean copyAsset(
        String assetName, String destFileName)
    {
        return copyAsset(assetName, destFileName, 1024);
    }

    public static boolean copyAsset(
        String assetName, String destFileName, int bufferSize)
    {
        InputStream input = null;
        FileOutputStream output = null;
        try {
            input = App.getContext().getAssets().open(assetName);
            output = new FileOutputStream(destFileName);

            final byte data[] = new byte[bufferSize];
            for (;;) {
                int count = input.read(data, 0, bufferSize);
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

    public static void logStream(String logTag, InputStream stream)
    {
        new Thread(new Runnable() {
            public void run() {
                Scanner sc = new Scanner(stream);
                while (sc.hasNextLine()) {
                    Log.i(logTag, sc.nextLine());
                }
            }
        }).start();
    }
}
