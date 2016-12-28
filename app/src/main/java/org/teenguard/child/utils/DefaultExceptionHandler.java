package org.teenguard.child.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;

import org.teenguard.child.service.ChildMonitoringService;

/**
 * Created by chris on 21/12/16.
 * http://chintanrathod.com/auto-restart-application-after-crash-forceclose-in-android/
 */

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;
    Activity activity;

    public DefaultExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        MyApp.dumpSharedPreferences();
        boolean isChild = MyApp.getSharedPreferences().getBoolean("IS-CHILD", false);
        boolean isChildConfigured = MyApp.getSharedPreferences().getBoolean("IS-CHILD-CONFIGURED", false);
        if (isChild && isChildConfigured) {
                Intent intent = new Intent(activity, ChildMonitoringService.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FILL_IN_SELECTOR);

            //    PendingIntent pendingIntent = PendingIntent.getActivity(MyApp.getInstance().getBaseContext(), 0, intent,Intent.FLAG_ACTIVITY_CLEAR_TOP); intent.getFlags());

                //Following code will restart your application after 2 seconds
                AlarmManager mgr = (AlarmManager) MyApp.getInstance().getBaseContext()
                        .getSystemService(Context.ALARM_SERVICE);
            //    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,pendingIntent);

                //This will finish your activity manually
                activity.finish();

                //This will stop your application and take out from it.
                System.exit(2);
        }
    }
}