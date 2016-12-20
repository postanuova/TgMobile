package org.teenguard.child.utils;

/**
 * Created by chris on 21/12/16.
 */

public class MyAppNotSingleton {
}
/*
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
/*public class MyApp extends Application {
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

  /*  public static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
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

 */