package org.teenguard.child.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by chris on 09/12/16.
 https://www.codeproject.com/articles/1070139/manage-app-permissions-on-android-marshmallow
 adb logcat new target SDK 21 doesn't support runtime permissions but the old target SDK 24 does.
 */

public abstract class PermissionUtils {

        public static boolean verifyPermissions(int[] grantResults) {
            // At least one result must be checked.
            if(grantResults.length < 1){
                return false;
            }

            // Verify that each required permission has been granted, otherwise return false.
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }

        public static boolean requestContactsPermissions(Activity activity) {

            boolean requestPermission;

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_CONTACTS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_CONTACTS)) {

                // Provide an additional rationale to the user if the permission was not granted
                // and the user would benefit from additional context for the use of the permission.
                // For example, if the request has been denied previously.

                requestPermission =  true;
            } else {
                // Contact permissions have not been granted yet. Request them directly.

                requestPermission =  true;
            }
            return requestPermission;
        }

    }

