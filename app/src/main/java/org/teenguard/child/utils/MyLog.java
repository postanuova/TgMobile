package org.teenguard.child.utils;

import android.util.Log;


/**
 * Created by chris on 11/10/16.
 */

public class MyLog {

    public static void i(Object o,String data) {
        Log.i(o.getClass().getName(),data);
    }

}
