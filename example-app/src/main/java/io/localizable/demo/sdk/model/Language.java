package io.localizable.demo.sdk.model;

import android.content.Context;
import android.util.SparseArray;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import io.localizable.demo.sdk.networking.LocalizableOperation;
import io.localizable.demo.sdk.networking.async.HttpRequest;
import io.localizable.demo.sdk.networking.callback.LocalizableAppLanguageCallback;
import io.localizable.demo.sdk.utils.LocalizableLog;
import okhttp3.Call;

import static io.localizable.demo.sdk.model.AppLanguage.*;

public class Language {
  String apiToken;
  String code;
  SparseArray<String> strings;
  SparseArray<String> appStrings;

  public Language(SparseArray<String> appStrings, Context context, String apiToken) {
    this.appStrings = appStrings;
    this.apiToken = apiToken;
    this.loadCachedData(context);
    this.checkForNewSupportedLanguages(context);
  }

  /**
   * Update language code, if the localizable language is the same only triggers a string update,
   * otherwise will clear current instance of the strings and execute a full request for the
   * selected language.
   *
   * @param languageCode New languageCode
   * @param context Application context
   */
  void setLanguage(String languageCode, Context context) {
    if (languageCode.equalsIgnoreCase(this.code)) {
      this.syncFromDisk(context);
      return;
    }
    this.code = languageCode;
    this.strings = new SparseArray<>();
    AppLanguage cleanAppLanguage = new AppLanguage(languageCode, new HashMap<String, String>());
    updateStrings(cleanAppLanguage, context);
  }


  /**
   * Update current instance with an appLanguage with all the string tokens
   *
   * @param appLanguage AppLanguage to override current instance, code and strings values
   */
  void updateInstanceWithAppLanguage(AppLanguage appLanguage) {
    this.code = appLanguage.code;
    this.strings = sparseArrayFromHashMap(appLanguage.strings, appStrings);
  }


  /**
   * Creates a SparseArray of Localizable strings with the key being the Int id of the String in the
   * strings file
   *
   * @param strings Localizable strings from Backend (key: string_id, value: string_value)
   * @param appStrings Local App resources sparse array (key: string_id, value: string_resource_id)
   * @return
   */
  private static SparseArray<String> sparseArrayFromHashMap(HashMap<String, String> strings,
                                                            SparseArray<String> appStrings) {
    try {
      SparseArray<String> localizableStrings = new SparseArray<>();
      for(int index = 0; index <= appStrings.size(); index ++) {
        int key = appStrings.keyAt(index);
        String value = appStrings.valueAt(index);
        String string = strings.get(value);
        if (string != null)
          localizableStrings.put(key ,string);
      }
      return localizableStrings;
    } catch(Exception e) {
      return null;
    }
  }


  /**
   * Check if there are any language updates from the server, if no new languages found only sync strings
   * otherwise call for a full fetch for the selected language
   *
   * @param context
   */
  void checkForNewSupportedLanguages(final Context context) {
    new SupportedLanguages(context, new SupportedLanguagesChangesCallback() {
      @Override
      void onNoChangesDetected() {
        Language.this.syncFromDisk(context);
      }

      @Override
      void onDefaultLanguageChanged(String newDefaultLanguageCode) {
        Language.this.setLanguage(newDefaultLanguageCode, context);
      }
    }, apiToken);
  }

  /**
   * Get a localized string given a String id and the formatting parameters.
   * If the string is loaded for the current LocalizableLanguage return that string otherwise
   * fallback to the string from resources
   *
   * @param context Application context
   * @param resID String resource identifier
   * @param parameters String parameters
   * @return Localizable string for current language or the system string for string resource identifier
   */
  public String getString(Context context, int resID, Object... parameters) {
    if (strings != null && strings.get(resID) != null)
      return String.format(strings.get(resID), parameters);
    return context.getResources().getString(resID, parameters);
  }

  /**
   * Get a localized string given a String id. If the string is loaded for the current LocalizableLanguage
   * return that string otherwise fallback to the string from resources.
   *
   * @param context Application context
   * @param resID String resource identifier
   * @return Localizable string for current language or the system string for string resource identifier
   */
  public String getString(Context context, int resID) {
    if (strings != null && strings.get(resID) != null)
      return strings.get(resID);
    return context.getResources().getString(resID);
  }


  /**
   * Load current language from cache and check if the localizable language changed, if so don't load
   * the strings and use the default project strings until a sync call comes
   * from the checkForNewSupportedLanguages()
   *
   * @param context Application context
   */
  void loadCachedData(Context context) {
    AppLanguage appLanguage = loadCurrentAppLanguageFromDisk(context);

    if (appLanguage == null) {
      LocalizableLog.error("Could not find any language on the disk");
      return;
    }

    this.code = appLanguage.code;
    this.strings = new SparseArray<>();

    String sdkLanguage = SupportedLanguages.currentLocalizableLanguageCodeFromCache(context);
    if (sdkLanguage == null) {
      LocalizableLog.debug("could not find any localizable language");
      return;
    }

    // Only set languages if the code is the same otherwise it will need an update or fetch
    // That is controlled by the checkForNewSupportedLanguages() method
    if (sdkLanguage.equalsIgnoreCase(appLanguage.code)) {
        updateInstanceWithAppLanguage(appLanguage);
    }
  }


  /**
   * Load current language from disk and sync with the server. The request only gives the diffed
   * data since last update
   *
   * @param context Application context
   */
  void syncFromDisk(Context context) {
    AppLanguage appLanguage = loadCurrentAppLanguageFromDisk(context);
    if (appLanguage == null) {
      LocalizableLog.error("Could not find any language on the disk");
      return;
    }
    updateInstanceWithAppLanguage(appLanguage);
    updateStrings(appLanguage, context);
  }


  /**
   * Load cached strings from file
   *
   * @param context Application context
   * @return Instance of the AppLanguage loaded from disk or null
   */
  private AppLanguage loadCurrentAppLanguageFromDisk(Context context) {
    return loadAppLanguageFromDisk(context);
  }


  /**
   * Request new string updates from Server, apply the diffs to the current instance and save the
   * file with the changes and updated timestamp
   *
   * @param appLanguage Current selected app language
   * @param context Application context
   */
  void updateStrings(final AppLanguage appLanguage, final Context context) {
    HttpRequest request = new HttpRequest(LocalizableOperation.UpdateLanguage(appLanguage.code, appLanguage.modifiedAt, apiToken));
    request.execute(new LocalizableAppLanguageCallback() {

      @Override
      protected void onResponse(Call call, AppLanguage serverDiffs) {
        if (serverDiffs.strings.isEmpty()) {
          LocalizableLog.debug("No updates from the server");
        } else {
          for (String key : serverDiffs.strings.keySet()) {
            appLanguage.strings.put(key, serverDiffs.strings.get(key));
          }
          updateInstanceWithAppLanguage(appLanguage);
        }

        appLanguage.modifiedAt = new Date().getTime()/1000;
        appLanguage.saveToDisk(context);
      }

      @Override
      public void onFailure(Call call, IOException e) {
        LocalizableLog.error(e);
      }
    });
  }


  @Override
  public String toString() {
    return "Language {" +
        "apiToken='" + apiToken + '\'' +
        ", code='" + code + '\'' +
        ", strings=" + strings.toString() +
        ", appStrings=" + appStrings.toString() +
        '}';
  }
}
