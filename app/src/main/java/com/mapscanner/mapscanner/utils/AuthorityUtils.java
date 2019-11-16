package com.mapscanner.mapscanner.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class AuthorityUtils {
    public static void getAlbumAuthority(Activity context){
        if (Build.VERSION.SDK_INT >= 23) {
            int write = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (write != PackageManager.PERMISSION_GRANTED || read != PackageManager.PERMISSION_GRANTED) {
                context.requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        300);
            }
        } else {
            Log.i("wytings", "------------- Build.VERSION.SDK_INT < 23 ------------");
        }
    }
}
