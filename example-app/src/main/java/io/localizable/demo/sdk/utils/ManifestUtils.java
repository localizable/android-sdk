package io.localizable.demo.sdk.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.localizable.demo.sdk.exceptions.NoSDKTokenFoundException;


/**
 * ManifestUtils serves the purpose of reading the meta-data fields of a given context's
 * AndroidManifest.xml file.
 */
public class ManifestUtils {

    public static final String SDK_TOKEN_METADATA_KEY = "io.localizable.SDK_TOKEN";

    /**
     * Retrieves the API Token from the metadata in the AndroidManifest.xml file.
     * <meta-data android:name="io.localizable.SDK_TOKEN" android:value="abc123"/>
     *
     * @param context Your application's context object.
     * @return The value of the meta-data "io.localizable.SDK_TOKEN" field.
     * @throws NoSDKTokenFoundException This exception is thrown when the API token is not found
     * in the AndroidManifest.xml file.
     */
    @Nonnull
    public static String getAPITokenFromMetadata(Context context) throws NoSDKTokenFoundException, PackageManager.NameNotFoundException {
        String apiToken = getMetadataString(context, SDK_TOKEN_METADATA_KEY);
        if (apiToken == null) {
            throw new NoSDKTokenFoundException();
        }
        return apiToken;
    }

    @Nullable
    private static String getMetadataString(@Nonnull Context context, @Nonnull String key) throws PackageManager.NameNotFoundException {
            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return applicationInfo.metaData.getString(key);
    }

}
