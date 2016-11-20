package org.teenguard.child.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by chris on 13/10/16.
 * http://stackoverflow.com/questions/987072/using-application-context-everywhere
 * http://stackoverflow.com/questions/9445661/how-to-get-the-context-from-anywhere

 * https://www.londonappdeveloper.com/how-to-use-git-hub-with-android-studio/
 */
/*
Useful for having app-context everywhere
 */
//added to manifest
public class MyApp extends Application {
    private static MyApp instance;
    //private static CookieManager cookieManager = CookieManager.getInstance();
    private static SharedPreferences preferences;
    public MyApp() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

   /* public static CookieManager getCookieManager() {
        return cookieManager;
    }*/

    public static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }
}
