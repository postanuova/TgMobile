package org.teenguard.child.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;


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

    private static Context mContext;

    public static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        instance = this;
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static MyApp getInstance() {
        return instance;
    }

    public static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(MyApp.getInstance().getApplicationContext());
    }

    public static void dumpSharedPreferences() {
        Map<String, ?> allEntries = getSharedPreferences().getAll();
        System.out.println("-------- SHARED PREFERENCES --------");
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().toString());
        }
        System.out.println("------------------------------------");
    }


    public static void resetSharedPreferences() {
        System.out.println("MyApp.resetSharedPreferences");
        MyApp.getSharedPreferences().edit()
                .putString("X-SESSID","dummy")
                .putBoolean("IS-CHILD", false)
                .putBoolean("IS-PARENT", false)
                .putBoolean("IS-CHILD-CONFIGURED", false)
                .putBoolean("IS-PARENT-CONFIGURED", false)
                .apply();
    }
}
