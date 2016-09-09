package io.localizable.sdk.model;

import android.content.Context;

import io.localizable.sdk.networking.HttpOperation;
import io.localizable.sdk.networking.LocalizableOperation;
import io.localizable.sdk.networking.async.HttpRequest;
import io.localizable.sdk.utils.FileLoader;
import io.localizable.sdk.utils.LocaleUtils;
import io.localizable.sdk.utils.LocalizableLog;
import io.localizable.sdk.utils.LocalizableSharedPreferences;
import io.localizable.sdk.utils.SupportedLanguagesCallback;
import io.localizable.sdk.utils.SupportedLanguagesChangesCallback;
import okhttp3.Call;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Supported languages by the App caches and returns the available languages codes.
 */
public class SupportedLanguages implements Serializable {
  private static final String FILENAME = "supportedLanguages.dex";

  /**
   * Supported languages by App.
   */
  private List<String> languages;

  /**
   * Given the list of supported languages, choose with will be used by the SDK to fetch the strings
   * from. Following Google's Localization fallback system of Android 7.0+.
   */
  private String defaultLanguageCode;


  /**
   * Callback to notify of state changes of the supported languages.
   */
  private transient SupportedLanguagesChangesCallback changesCallback;

  public String getDefaultLanguageCode() {
    return defaultLanguageCode;
  }

  public List<String> getLanguages() {
    return languages;
  }

  public SupportedLanguagesChangesCallback getChangesCallback() {
    return changesCallback;
  }

  /**
   * Create an empty list of strings and the default code contained in the SharedPreferences.
   *
   * @param context Application context
   */
  private SupportedLanguages(Context context) {
    this.languages = new LinkedList<>();
    this.defaultLanguageCode = LocalizableSharedPreferences.getDefaultLocalizableLanguage(context);
  }

  /**
   * Returns the Localizable language code for the current device language implementing google
   * Android 7.0 fallbacks
   *
   * @param context Application context
   * @return Localizable language code for cached supported languages
   */
  public static String currentLocalizableLanguageCodeFromCache(Context context) {
    SupportedLanguages loadedLanguages = loadStringsFromFile(context);
    if (loadedLanguages == null) {
      return null;
    }
    return LocaleUtils.selectDefaultLanguageCode(loadedLanguages.languages, context);
  }

  /**
   * Returns all the cached supported Locales.
   * @param context Application context
   * @return List of supported locales
   */
  public static List<Locale> cachedSupportedLanguages(Context context) {
    SupportedLanguages loadedLanguages = loadStringsFromFile(context);
    LinkedList<Locale> result = new LinkedList<>();
    for (String localeTag : loadedLanguages.languages) {
      Locale locale = LocaleUtils.localeFromTag(localeTag);
      if (locale == null) {
        continue;
      }
      result.add(locale);
    }
    return result;
  }

  /**
   * <p>
   * Creates an instance of Localizable supported languages, first loads current supported
   * languages from disk and afterwards sync with server.
   *</p>
   * <p>
   * This class has the callback to notify of new languages from server.
   * </p>
   *
   * @param context Application context
   * @param callback Callback to notify the language state changes
   * @param apiToken Localizable Api token
   */
  public SupportedLanguages(Context context, SupportedLanguagesChangesCallback callback,
                            String apiToken) {
    SupportedLanguages loadedLanguages = loadStringsFromFile(context);
    this.languages = loadedLanguages.getLanguages();
    this.defaultLanguageCode = loadedLanguages.getDefaultLanguageCode();
    this.changesCallback = callback;
    sync(context, apiToken);
  }

  private void sync(final Context context, String apiToken) {
    HttpOperation operation = LocalizableOperation.languageCodes(apiToken);
    new HttpRequest(operation).execute(new SupportedLanguagesCallback() {

      @Override
      public void onFailure(Call call, IOException exception) {
        LocalizableLog.warning(exception);
        if (getChangesCallback() != null) {
          getChangesCallback().errorSyncing();
        }
      }

      @Override
      protected void onDefaultLanguageChanged(List<String> languages) {
        handleLanguageCodes(context, languages);
      }
    });
  }

  /**
   * Handles the result of a request for sync Localizable locale changes, checks if there are any
   * updates on the current selected language and calls the respective callbacks.
   *
   * @param context Application context
   * @param serverCodes most recent list of Localizable supported language codes
   */
  void handleLanguageCodes(Context context, List<String> serverCodes) {
    String newSelectedLanguage = LocaleUtils.selectDefaultLanguageCode(serverCodes, context);
    if (newSelectedLanguage == null) {
      LocalizableLog.error("Cannot match localizable languages with device languages");
      if (changesCallback != null) {
        changesCallback.errorSyncing();
      }
      return;
    }

    if (newSelectedLanguage.equalsIgnoreCase(defaultLanguageCode)
        && LocaleUtils.listOfLanguageCodesEquals(getLanguages(), serverCodes)) {
      if (changesCallback != null) {
        changesCallback.onNoChangesDetected(newSelectedLanguage);
      }
      return;
    }

    LocalizableSharedPreferences.setDefaultLocalizableLanguage(newSelectedLanguage, context);
    defaultLanguageCode = newSelectedLanguage;
    languages = serverCodes;
    if (changesCallback != null) {
      changesCallback.onDefaultLanguageChanged(defaultLanguageCode);
    }
    saveToFile(context);
  }


  /**
   * Store list to file.
   *
   * @param context Application context.
   */
  private void saveToFile(Context context) {
    new FileLoader<SupportedLanguages>(context, FILENAME).store(this);
  }

  /**
   * Load SupportedLanguages class from file.
   *
   * @param context Application context
   * @return Loaded class from file or a new instance with empty list of locales
   */
  private static SupportedLanguages loadStringsFromFile(Context context) {
    SupportedLanguages localFile = new FileLoader<SupportedLanguages>(context, FILENAME).loadFile();
    if (localFile == null) {
      localFile = new SupportedLanguages(context);
    }
    return localFile;
  }

  /**
   * Delete the current cached file for SupportedLanguages.
   *
   * @param context Application context
   */
  public static void deleteAppLanguageFromDisk(Context context) {
    new FileLoader<SupportedLanguages>(context, FILENAME).delete();
  }
}
