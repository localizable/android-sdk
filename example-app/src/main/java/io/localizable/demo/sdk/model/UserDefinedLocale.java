package io.localizable.demo.sdk.model;

import android.content.Context;

import java.io.Serializable;
import java.util.Locale;

import io.localizable.demo.sdk.utils.FileLoader;

public class UserDefinedLocale implements Serializable {
  private static String filename = "userLocale.dex";
  private static Locale userLocale;

  public static Locale userLocale(Context context) {
    if (userLocale == null) {
      userLocale = new FileLoader<Locale>(context, filename).loadFile();
    }
    return userLocale;
  }

  public static void setUserLocale(Context context, Locale locale) {
    userLocale = locale;
    if (locale == null) {
      new FileLoader<Locale>(context, filename).delete();
      return;
    }
    new FileLoader<Locale>(context, filename).store(locale);
  }
}
