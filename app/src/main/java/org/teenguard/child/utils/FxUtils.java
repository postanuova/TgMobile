package org.teenguard.child.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import org.teenguard.child.R;

/**
 * Created by chris on 01/12/16.
 */

public class FxUtils {

    public static void vibe() {
        Vibrator vibe = (Vibrator) MyApp.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(50);
    }

    public static void shake(View viewToShake) {
        Animation shake = AnimationUtils.loadAnimation(MyApp.getContext(), R.anim.shake);
        viewToShake.startAnimation(shake);
    }

    public static void asyncToast(final String message) {
        //Let this be the code in your n'th level thread from main UI thread
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            public void run() {
                Toast.makeText(MyApp.getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
