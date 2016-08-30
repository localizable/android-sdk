package io.localizable.demo.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

/**
 * Utils serves many purposes, but mostly loading and saving objects/strings to file or
 * to the SharedPreferences. It also provides utility methods to generate random UUIDs and
 * sanitizing routes.
 */
public class Utils {

    // Localizable SharedPreferences key
    private static final String SHARED_PREFERENCES_STRING_KEY = "io.localizable.UUID";

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

    /**
     * Loads a string from the SharedPreferences with a given key.
     *
     * @param key     The key associated to the string value.
     * @param context A context object.
     * @return The string in case it exists, null otherwise.
     */
    public static String getString(String key, Context context) {
        try {
            SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCES_STRING_KEY,
                    Context.MODE_PRIVATE);
            return sharedPreferences.getString(key, null);
        } catch (NullPointerException e) {
            LocalizableLog.error(e);
            return null;
        }
    }
}
