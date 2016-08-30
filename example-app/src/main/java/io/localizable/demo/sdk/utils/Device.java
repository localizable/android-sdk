package io.localizable.demo.sdk.utils;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Device is a helper class to get all the necessary information of the user's Device.
 */
public class Device {

    // Platform name
    private static final String PLATFORM = "Android";

    // Shared preferences keys
    private static final String UUID_KEY = "UUID";

    // String values for connectivity state
    private static final String CONNECTIVITY_WIFI = "Wifi";
    private static final String CONNECTIVITY_CELLULAR = "Cellular";
    private static final String CONNECTIVITY_NO_CONNECTIVITY = "No Connectivity";
    private static final String CONNECTIVITY_NO_PERMISSION = "No Permission";

    /**
     * Returns the vendor of the device.
     *
     * @return The vendor of the device.
     */
    public static String getVendor() {
        return Build.MANUFACTURER;
    }

    /**
     * Returns the Android platform.
     *
     * @return The Android platform.
     */
    public static String getPlatform() {
        return PLATFORM;
    }

    /**
     * Returns the model of the device.
     *
     * @return The model of the device.
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * Returns the Android version of the device.
     *
     * @return The Android version.
     */
    public static String getSystemVersion() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    /**
     * Returns the Android release version (like 5.1) of the device.
     *
     * @return The Android release version.
     */
    public static String getSystemReleaseVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * Returns the current internet connectivity of the device.
     *
     * @param context A context object.
     * @return The current internet connectivity of the device.
     */
    public static String getInternetConnectivity(Context context) {
        try {
            if (Utils.hasPermission(Manifest.permission.ACCESS_NETWORK_STATE, context)) {
                ConnectivityManager connManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mNetwork = connManager
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (mWifi != null && mWifi.isConnected()) {
                    return CONNECTIVITY_WIFI;
                } else if (mNetwork != null && mNetwork.isConnected()) {
                    return CONNECTIVITY_CELLULAR;
                } else {
                    return CONNECTIVITY_NO_CONNECTIVITY;
                }
            } else {
                return CONNECTIVITY_NO_PERMISSION;
            }
        } catch (Exception e) {
            return CONNECTIVITY_NO_CONNECTIVITY;
        }
    }

    /**
     * Checks if the device has internet connectivity regardless how it is connected.
     *
     * @param context A context object.
     * @return true if the device has internet connectivity and false otherwise.
     */
    public static boolean hasInternetConnectivity(Context context) {
        String internetConnectivity = getInternetConnectivity(context);
        return internetConnectivity.equals(CONNECTIVITY_CELLULAR)
                || internetConnectivity.equals(CONNECTIVITY_WIFI)
                || internetConnectivity.equals(CONNECTIVITY_NO_PERMISSION);
    }

    /**
     * Converts all the Device information into a JSONObject to be sent to the Hoko backend
     * service.
     *
     * @param context A context object.
     * @return The JSONObject representation of Device.
     */
    public static JSONObject json(Context context) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putOpt("vendor", getVendor());
            jsonObject.putOpt("platform", getPlatform());
            jsonObject.putOpt("model", getModel());
            jsonObject.putOpt("system_version", getSystemVersion());
            return jsonObject;
        } catch (JSONException e) {
            LocalizableLog.error(e);
        }
        return null;
    }

}
