package com.xptschool.parent.common;

import android.content.Context;
import android.support.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

/**
 * Created by dexing on 2017/9/13 0013.
 * No1
 */

public class MyModulePreference extends TrayPreferences {

    private static final String FILE_NAME = "com.xptschool.parent";

    public MyModulePreference(@NonNull Context context) {
        super(context, FILE_NAME, 1);
    }

}
