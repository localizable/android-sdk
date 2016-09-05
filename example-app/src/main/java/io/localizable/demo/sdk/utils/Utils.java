package io.localizable.demo.sdk.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public class Utils {
    /**
     * Checks where the application has a given permission granted on the AndroidManifest.xml file.
     *
     * @param permission The permission to be checked.
     * @param context    A context object.
     * @return true if has permission, false otherwise.
     */
    public static boolean hasPermission(String permission, Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager.checkPermission(permission, context.getPackageName())
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                LocalizableLog.error("Requesting permission " + permission
                        + " but it is not on the AndroidManifest.xml");
                return false;
            }
        } catch (Exception e) {
            LocalizableLog.error(e.toString());
            return false;
        }
    }
}
