package io.localizable.sdk.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import io.localizable.sdk.exceptions.LocalizableMetadataNotFoundException;
import io.localizable.sdk.exceptions.NoSdkTokenFoundException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * ManifestUtils serves the purpose of reading the meta-data
 * fields of a given context's AndroidManifest.xml file.
 */
public final class ManifestUtils {

  /**
   * Manifest metadata key to look for.
   */
  public static final String SDK_TOKEN_METADATA_KEY = "io.localizable.SDK_TOKEN";


  /**
   * Retrieves the API Token from the metadata in the AndroidManifest.xml file.
   * meta-data android:name="io.localizable.SDK_TOKEN" android:value="abc123"
   *
   * @param context Your application's context object.
   * @return The value of the meta-data "io.localizable.SDK_TOKEN" field.
   * @throws NoSdkTokenFoundException This exception is thrown when the API token is not found
   *      in the AndroidManifest.xml file
   */
  public static @Nonnull String getApiTokenFromMetadata(Context context) {
    String apiToken = getMetadataString(context, SDK_TOKEN_METADATA_KEY);
    if (apiToken == null) {
      throw new NoSdkTokenFoundException();
    }
    return apiToken;
  }

  /**
   * Try to fetch the metadata for the current application context.
   *
   * @param context Application context
   * @param key Manifest meta-data key
   * @return Meta-data key value or throw exception if not found
   */
  static @Nullable String getMetadataString(@Nonnull Context context, @Nonnull String key) {
    try {
      ApplicationInfo applicationInfo = context.getPackageManager()
          .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
      return applicationInfo.metaData.getString(key);
    } catch (PackageManager.NameNotFoundException ignored) {
      throw new LocalizableMetadataNotFoundException();
    }
  }
}
