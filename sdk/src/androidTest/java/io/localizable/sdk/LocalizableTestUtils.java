package io.localizable.sdk;

import android.content.Context;

import io.localizable.sdk.model.AppLanguage;
import io.localizable.sdk.model.SupportedLanguages;
import io.localizable.sdk.model.UserDefinedLocale;

import java.util.Locale;

public class LocalizableTestUtils {

  /**
   * Deletes Localizable cached files.
   *
   * @param context Application context
   */
  public static void deleteAllFiles(Context context) {
    SupportedLanguages.deleteAppLanguageFromDisk(context);
    AppLanguage.deleteAppLanguageFromDisk(context);
    UserDefinedLocale.clearCache(context);
  }

  /**
   * Sets the application context to use english as main language.
   *
   * @param context Application context
   */
  public static void setBaseLanguageEnglish(Context context) {
    Localizable.setApplicationContextLocale(context, Locale.ENGLISH);
  }
}
