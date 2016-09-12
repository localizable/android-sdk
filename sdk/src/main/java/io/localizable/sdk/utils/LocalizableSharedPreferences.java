package io.localizable.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

@SuppressWarnings("unused")
public class LocalizableSharedPreferences {

  private static final String DEFAULT_LANGUAGE_CODE = "io.localizable.default.language_code";

  public static boolean clear(Context context) {
    return Helper.clear(context);
  }

  public static void setDefaultLocalizableLanguage(String currentLocalizableLanguageCode,
                                                   Context context) {
    Helper.setString(DEFAULT_LANGUAGE_CODE, currentLocalizableLanguageCode, context);
  }

  public static String getDefaultLocalizableLanguage(Context context) {
    return Helper.getString(DEFAULT_LANGUAGE_CODE, context);
  }

  private static class Helper {
    // Localizable SharedPreferences key
    private static final String SHARED_PREFERENCES_STRING_KEY = "io.localizable.UUID";

    static boolean clear(Context context) {
      return context.getSharedPreferences(SHARED_PREFERENCES_STRING_KEY,
          Context.MODE_PRIVATE).edit().clear().commit();
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
      } catch (NullPointerException exception) {
        LocalizableLog.error(exception);
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
      } catch (NullPointerException exception) {
        LocalizableLog.error(exception);
        return false;
      }
    }
  }
}
