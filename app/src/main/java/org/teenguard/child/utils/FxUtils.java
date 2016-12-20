package org.teenguard.child.utils;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
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
        Vibrator vibe = (Vibrator) MyApp.getInstance().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(50);
    }

    public static void shake(View targetView) {
        Animation shake = AnimationUtils.loadAnimation(MyApp.getInstance().getApplicationContext(), R.anim.shake);
        targetView.startAnimation(shake);
    }

    public static void changeColor(View targetView,int startColor, int endColor, int duration) {
        ObjectAnimator.ofObject(targetView, "backgroundColor", new ArgbEvaluator(), startColor, endColor)
                .setDuration(duration)
                .start();
    }

    public static void asyncToast(final String message) {
        //Let this be the code in your n'th level thread from main UI thread
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            public void run() {
                Toast.makeText(MyApp.getInstance().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
