package io.localizable.sdk.model;

import static io.localizable.sdk.model.AppLanguage.loadAppLanguageFromDisk;

import android.content.Context;
import android.util.SparseArray;

import io.localizable.sdk.networking.LocalizableOperation;
import io.localizable.sdk.networking.async.HttpRequest;
import io.localizable.sdk.networking.callback.LocalizableAppLanguageCallback;
import io.localizable.sdk.utils.LocalizableLog;

import io.localizable.sdk.utils.SupportedLanguagesChangesCallback;
import okhttp3.Call;

import java.io.IOException;
import java.util.HashMap;

/**
 * Control the localized tokens for the current defined localizable language,
 * Fetches the available languages from the server and selects
 * the correct one following Google's fallback system from android 7.0,
 * Updates the strings each time an instance is instantiated,
 * by diffing changes since last update.
 */
public final class Language {

  /**
   * Localizable API token.
   */
  private String localizableApiToken;

  /**
   * Current locale code, example: pt-PT.
   */
  private String currentLanguageCode;

  /**
   * Current localizable string, keys are the R int values and the
   * values are the server side translations.
   */
  private SparseArray<String> localizableStrings;

  /**
   * Application resource strings, keys are the string "name"
   * and the values the string R int values.
   */
  private SparseArray<String> applicationStrings;

  /**
   * Create a new instance of language, loading cached data if
   * the current language is the same, and checks for string updates.
   *
   * @param appStrings Application strings from resources
   * @param context Application context
   * @param apiToken Localizable API token
   */
  public static Language loadLanguage(SparseArray<String> appStrings, Context context,
                                      String apiToken) {
    return new Language(appStrings, apiToken).init(context);
  }


  /**
   * Create a new instance of language, loading cached data if
   * the current language is the same, and checks for string updates.
   *
   * @param appStrings Application strings from resources
   * @param apiToken Localizable API token
   */
  private Language(SparseArray<String> appStrings, String apiToken) {
    applicationStrings = appStrings;
    localizableApiToken = apiToken;
    localizableStrings = null;
    currentLanguageCode = null;
  }


  /**
   * Init with loaded data from disk and fetch updates after loading from disk.
   *
   * @param context Application context
   * @return current instance of Language
   */
  private Language init(Context context) {
    this.loadCachedData(context);
    this.checkForNewSupportedLanguages(context);
    return this;
  }


  /**
   * Update language code, if the localizable language is the same
   * only triggers a string update,
   * otherwise will clear current instance of the strings
   * and execute a full request for the selected language.
   *
   * @param languageCode New languageCode
   * @param context Application context
   */
  void setLanguage(String languageCode, Context context) {
    if (languageCode.equalsIgnoreCase(this.currentLanguageCode)) {
      this.syncFromDisk(context);
      return;
    }
    this.currentLanguageCode = languageCode;
    this.localizableStrings = new SparseArray<>();
    AppLanguage cleanAppLanguage = new AppLanguage(languageCode, new HashMap<String, String>());
    updateStrings(cleanAppLanguage, context);
  }


  /**
   * Update current instance with an appLanguage with all the string tokens.
   *
   * @param appLanguage AppLanguage to override current instance, code and strings values
   */
  void updateInstanceWithAppLanguage(AppLanguage appLanguage) {
    this.currentLanguageCode = appLanguage.getCode();
    this.localizableStrings = sparseArrayFromHashMap(appLanguage.getStrings(), applicationStrings);
  }


  /**
   * Creates a SparseArray of Localizable strings with the key being the Int id of the String in the
   * strings file.
   *
   * @param strings Localizable strings from Backend (key: string_id, value: string_value)
   * @param appStrings Local App resources sparse array (key: string_id, value: string_resource_id)
   * @return SparseArray of Localizable strings
   */
  private static SparseArray<String> sparseArrayFromHashMap(final HashMap<String, String> strings,
                                                            final SparseArray<String> appStrings) {
    try {
      SparseArray<String> localizableStrings = new SparseArray<>();
      for (int index = 0; index <= appStrings.size(); index++) {
        int key = appStrings.keyAt(index);
        String value = appStrings.valueAt(index);
        String string = strings.get(value);
        if (string != null) {
          localizableStrings.put(key, string);
        }
      }
      return localizableStrings;
    } catch (Exception exception) {
      LocalizableLog.warning(exception);
      return null;
    }
  }


  /**
   * Check if there are any language updates from the server,
   * if no new languages found only sync strings otherwise
   * call for a full fetch for the selected language.
   *
   * @param context Application context
   */
  void checkForNewSupportedLanguages(final Context context) {
    new SupportedLanguages(context, new SupportedLanguagesChangesCallback() {
      @Override
      public void onNoChangesDetected() {
        Language.this.syncFromDisk(context);
      }

      @Override
      public void onDefaultLanguageChanged(final String newDefaultLanguageCode) {
        Language.this.setLanguage(newDefaultLanguageCode, context);
      }
    }, localizableApiToken);
  }


  /**
   * Get a localized string given a String id and the formatting parameters.
   * If the string is loaded for the current LocalizableLanguage
   * return that string otherwise fallback to the string from resources.
   *
   * @param context Application context
   * @param resId String resource identifier
   * @param parameters String parameters
   * @return Localizable string for current language or the default resource in values
   */
  public String getString(final Context context, final int resId, final Object... parameters) {
    if (localizableStrings != null && localizableStrings.get(resId) != null) {
      return String.format(localizableStrings.get(resId), parameters);
    }
    return context.getResources().getString(resId, parameters);
  }


  /**
   * Get a localized string given a String id.
   * If the string is loaded for the current LocalizableLanguage
   * return that string otherwise fallback to the string from resources.
   *
   * @param context Application context
   * @param resId String resource identifier
   * @return Localizable string for current language or the default resource in values
   */
  public String getString(final Context context, final int resId) {
    if (localizableStrings != null && localizableStrings.get(resId) != null) {
      return localizableStrings.get(resId);
    }
    return context.getResources().getString(resId);
  }


  /**
   * Load current language from cache and check if the localizable language changed,
   * if so don't load the strings and use the default project strings until a sync call comes
   * from the checkForNewSupportedLanguages().
   *
   * @param context Application context
   */
  void loadCachedData(final Context context) {
    AppLanguage appLanguage = loadCurrentAppLanguageFromDisk(context);

    if (appLanguage == null) {
      LocalizableLog.error("Could not find any language on the disk");
      return;
    }

    this.currentLanguageCode = appLanguage.getCode();

    String sdkLanguage = SupportedLanguages.currentLocalizableLanguageCodeFromCache(context);
    if (sdkLanguage == null) {
      LocalizableLog.debug("could not find any localizable language");
      return;
    }

    // Only set languages if the code is the same otherwise it will need an update or fetch
    // That is controlled by the checkForNewSupportedLanguages() method
    if (sdkLanguage.equalsIgnoreCase(appLanguage.getCode())) {
      updateInstanceWithAppLanguage(appLanguage);
    }
  }


  /**
   * Load current language from disk and sync with the server. The request only gives the diffed
   * data since last update
   *
   * @param context Application context
   */
  void syncFromDisk(final Context context) {
    AppLanguage appLanguage = loadCurrentAppLanguageFromDisk(context);
    if (appLanguage == null) {
      LocalizableLog.error("Could not find any language on the disk");
      return;
    }
    updateInstanceWithAppLanguage(appLanguage);
    updateStrings(appLanguage, context);
  }


  /**
   * Load cached strings from file.
   *
   * @param context Application context
   * @return Instance of the AppLanguage loaded from disk or null
   */
  private AppLanguage loadCurrentAppLanguageFromDisk(final Context context) {
    return loadAppLanguageFromDisk(context);
  }


  /**
   * Request new string updates from Server, apply the diffs
   * to the current instance and save the
   * file with the changes and updated timestamp.
   *
   * @param appLanguage Current selected app language
   * @param context Application context
   */
  void updateStrings(final AppLanguage appLanguage, final Context context) {
    HttpRequest request = new HttpRequest(LocalizableOperation.updateLanguage(appLanguage.getCode(),
        appLanguage.getModifiedAt(), localizableApiToken));
    request.execute(new LocalizableAppLanguageCallback() {

      @Override
      protected void onResponse(final Call call, final AppLanguage serverDiffs) {
        if (serverDiffs.getStrings().isEmpty()) {
          LocalizableLog.debug("No updates from the server");
        } else {
          appLanguage.update(serverDiffs);
          updateInstanceWithAppLanguage(appLanguage);
          appLanguage.saveToDisk(context);
        }
      }

      @Override
      public void onFailure(final Call call, final IOException exception) {
        LocalizableLog.error(exception);
      }
    });
  }


  @Override
  public String toString() {
    return "Language {"
        + "apiToken='" + localizableApiToken + '\''
        + ", code='" + currentLanguageCode + '\''
        + ", strings=" + localizableStrings.toString()
        + ", appStrings=" + applicationStrings.toString()
        + '}';
  }
}
