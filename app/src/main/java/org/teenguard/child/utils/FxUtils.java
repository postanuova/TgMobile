package org.teenguard.child.utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by chris on 01/12/16.
 */

public class FxUtils {

    public static void vibe() {
        Vibrator vibe = (Vibrator) MyApp.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(50);
    }
}
