package io.localizable.demo.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalizableSharedPreferences {
  private static final String DEFAULT_LOCALIZABLE_LANGUAGE_CODE = "io.localizable.default.language_code";
  private static final String USER_LOCALIZABLE_LANGUAGE_CODE = "io.localizable.default.language_code";

  public static void setDefaultLocalizableLanguage(String currentLocalizableLanguageCode, Context context) {
    Helper.setString(DEFAULT_LOCALIZABLE_LANGUAGE_CODE, currentLocalizableLanguageCode, context);
  }

  public static String getDefaultLocalizableLanguage(Context context) {
    return Helper.getString(DEFAULT_LOCALIZABLE_LANGUAGE_CODE, context);
  }


  public static void setUserLocalizableLanguage(String currentLocalizableLanguageCode, Context context) {
    Helper.setString(USER_LOCALIZABLE_LANGUAGE_CODE, currentLocalizableLanguageCode, context);
  }

  public static String getUserLocalizableLanguage(Context context) {
    return Helper.getString(USER_LOCALIZABLE_LANGUAGE_CODE, context);
  }


  private static class Helper {
    // Localizable SharedPreferences key
    private static final String SHARED_PREFERENCES_STRING_KEY = "io.localizable.UUID";

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


    /**
     * Sets a string from the SharedPreferences with a given key.
     *
     * @param key     The key associated to the string value.
     * @param context A context object.
     * @return The string in case it exists, null otherwise.
     */
    public static boolean setString(String key, String newValue, Context context) {
      try {
        SharedPreferences sharedPreferences =
            context.getSharedPreferences(SHARED_PREFERENCES_STRING_KEY,
                Context.MODE_PRIVATE);
        return sharedPreferences.edit().putString(key, newValue).commit();
      } catch (NullPointerException e) {
        LocalizableLog.error(e);
        return false;
      }
    }
  }
}
