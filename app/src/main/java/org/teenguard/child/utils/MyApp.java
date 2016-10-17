package org.teenguard.child.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by chris on 13/10/16.
 * http://stackoverflow.com/questions/987072/using-application-context-everywhere
 * http://stackoverflow.com/questions/9445661/how-to-get-the-context-from-anywhere
 */
/*
Useful for having app-context everywhere
 */
//added to manifest
public class MyApp extends Application {
    private static MyApp instance;

    public MyApp() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

}
