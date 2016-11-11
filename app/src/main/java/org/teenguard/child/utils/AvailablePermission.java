package org.teenguard.child.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.util.Log;

import java.util.List;

/**
 * Created by chris on 11/11/16.
 */

public class AvailablePermission {
    public void listPermissions() {
        System.out.println("not tested");
        Context context = MyApp.getContext();
        PackageManager pm = context.getPackageManager();
        CharSequence csPermissionGroupLabel;
        CharSequence csPermissionLabel;

        List<PermissionGroupInfo> lstGroups = pm.getAllPermissionGroups(0);
        for (PermissionGroupInfo pgi : lstGroups) {
            csPermissionGroupLabel = pgi.loadLabel(pm);
            Log.e("perm", pgi.name + ": " + csPermissionGroupLabel.toString());

            try {
                List<PermissionInfo> lstPermissions = pm.queryPermissionsByGroup(pgi.name, 0);
                for (PermissionInfo pi : lstPermissions) {
                    csPermissionLabel = pi.loadLabel(pm);
                    Log.e("perm", "   " + pi.name + ": " + csPermissionLabel.toString());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
