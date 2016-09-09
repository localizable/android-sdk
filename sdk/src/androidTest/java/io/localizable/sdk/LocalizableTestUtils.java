package io.localizable.sdk;

import android.content.Context;

import java.util.Locale;

import io.localizable.sdk.model.AppLanguage;
import io.localizable.sdk.model.SupportedLanguages;


public class LocalizableTestUtils {


  /**
   * Deletes Localizable cached files.
   *
   * @param context Application context
   */
  public static void deleteAllFiles(Context context) {
    SupportedLanguages.deleteAppLanguageFromDisk(context);
    AppLanguage.deleteAppLanguageFromDisk(context);
  }

  /**
   * Sets the application context to use english as main language.
   *
   * @param context Application context
   */
  public static void setBaseLanguageEnglish(Context context) {
    Localizable.setApplicationContextLocale(context, Locale.ENGLISH);
  }


  /**
   * Wait for the Network requests to complete.
   */
  public static void waitForSetup() {
    sleep(10);
  }

  /**
   * Sleeps for x seconds.
   *
   * @param milliseconds Number of seconds to sleep
   */
  private static void sleep(final int milliseconds) {
    int currentMillis = 0;
    while (currentMillis < milliseconds) {
      currentMillis += 100;
      try {
        Thread.sleep(100);
      } catch (InterruptedException ignored) {
        break;
      }
    }
  }
}
