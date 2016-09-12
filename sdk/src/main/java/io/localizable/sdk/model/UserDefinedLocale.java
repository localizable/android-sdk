package io.localizable.sdk.model;

import android.content.Context;

import io.localizable.sdk.utils.FileLoader;

import java.io.Serializable;
import java.util.Locale;

public class UserDefinedLocale implements Serializable {
  private static String filename = "userLocale.dex";
  private static Locale userLocale;

  /**
   * Current Localizable cached locale.
   *
   * @param context Application context
   * @return Localizable cached locale
   */
  public static Locale userLocale(Context context) {
    if (userLocale == null) {
      userLocale = new FileLoader<Locale>(context, filename).loadFile();
    }
    return userLocale;
  }

  /**
   * Saves to disk the current Localizable selected locale.
   *
   * @param context Application context
   * @param locale Locale to store
   */
  public static void setUserLocale(Context context, Locale locale) {
    userLocale = locale;
    if (locale == null) {
      new FileLoader<Locale>(context, filename).delete();
      return;
    }
    new FileLoader<Locale>(context, filename).store(locale);
  }

  /**
   * Deletes the stored locale from disk.
   *
   * @param context Application context
   */
  public static void clearCache(Context context) {
    userLocale = null;
    new FileLoader<Locale>(context, filename).delete();
  }
}
