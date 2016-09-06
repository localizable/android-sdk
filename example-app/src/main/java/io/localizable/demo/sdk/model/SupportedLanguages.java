package io.localizable.demo.sdk.model;

import android.content.Context;
import android.os.Build;
import android.os.LocaleList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.localizable.demo.sdk.Localizable;
import io.localizable.demo.sdk.networking.HttpOperation;
import io.localizable.demo.sdk.networking.LocalizableOperation;
import io.localizable.demo.sdk.networking.async.HttpRequest;
import io.localizable.demo.sdk.networking.async.JSONCallback;
import io.localizable.demo.sdk.utils.FileLoader;
import io.localizable.demo.sdk.utils.LocalizableLog;
import io.localizable.demo.sdk.utils.LocalizableSharedPreferences;
import okhttp3.Call;

public class SupportedLanguages implements Serializable {
  private static String fileName = "supportedLanguages.dex";
  private List<String> languages;
  private String defaultLanguageCode;

  private transient SupportedLanguagesChangesCallback changesCallback;

  public String getDefaultLanguageCode() {
    return defaultLanguageCode;
  }

  public List<String> getLanguages() {
    return languages;
  }

  private SupportedLanguages(Context context) {
    this.languages = new LinkedList<>();
    this.defaultLanguageCode = LocalizableSharedPreferences.getDefaultLocalizableLanguage(context);
  }

  /**
   * Return the Localizable language code for the current device language implementing google
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
    return selectDefaultLanguageCode(loadedLanguages.languages, context);
  }

  public SupportedLanguages(Context context, SupportedLanguagesChangesCallback callback, String apiToken) {
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
      public void onFailure(Call call, IOException e) {
        changesCallback.errorSyncing();
      }

      @Override
      void onDefaultLanguageChanged(List<String> languages) {
        handleLanguageCodes(context, languages);
      }
    });
  }

  public void handleLanguageCodes(Context context, List<String> serverCodes) {
    String newSelectedLanguage = selectDefaultLanguageCode(serverCodes, context);
    if (newSelectedLanguage == null) {
      LocalizableLog.error("Cannot match localizable languages with device languages");
      changesCallback.errorSyncing();
      return;
    }

    if (newSelectedLanguage.equalsIgnoreCase(defaultLanguageCode) && listOfLanguageCodesEquals(serverCodes)) {
      changesCallback.onNoChangesDetected();
      return;
    }

    defaultLanguageCode = newSelectedLanguage;
    languages = serverCodes;
    changesCallback.onDefaultLanguageChanged(defaultLanguageCode);
    saveToFile(context);
  }

  public boolean listOfLanguageCodesEquals(List<String> serverCodes) {

    if (serverCodes.size() != getLanguages().size()) {
      return false;
    }

    for (String code : getLanguages()) {
      if (!serverCodes.contains(code)) {
        return false;
      }
    }

    return true;
  }

  private static String selectDefaultLanguageCode(List<String> languages, Context context) {
    return localizableDefaultLocale(deviceLocales(context), languages);
  }

  private static String localizableDefaultLocale(List<Locale> deviceLocales, List<String> localizableLocales) {
    for (Locale locale : deviceLocales) {
      String selectedLocale = localizableLocaleFromLocale(locale, localizableLocales);
      if (selectedLocale != null) {
        return selectedLocale;
      }
    }

    if (localizableLocales.contains(Localizable.Constants.LOCALIZABLE_DEFAULT_LANGUAGE_CODE)) {
      return Localizable.Constants.LOCALIZABLE_DEFAULT_LANGUAGE_CODE;
    }

    LocalizableLog.error("Could not find any matching languages from user selected languages");
    return null;
  }


  /**
   * Return the list of device locales (See android 7+ locales), ordered by priority. If the user explicitly setted
   * the Localizable language that language is the first one in order
   *
   * @param context Application context
   * @return List of device locales by order of priority and if a language was set by user that one is
   * the first
   */
  private static List<Locale> deviceLocales(Context context) {
    List<Locale> locales = new LinkedList<>();

    if (UserDefinedLocale.userLocale(context) != null) {
      locales.add(UserDefinedLocale.userLocale(context));
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      LocaleList list = context.getResources().getConfiguration().getLocales();
      for (int i = 0; i < list.size(); i++) {
        locales.add(list.get(i));
      }
    } else {
      locales.add(context.getResources().getConfiguration().locale);
    }
    return locales;
  }

  /**
   *
   * Find a match for a locale in a list of locales, using google android 7.0 fallback system.
   *
   * https://developer.android.com/guide/topics/resources/multilingual-support.html
   *
   * @param locale Locale to match
   * @param localizableLocales Possible list of localizable locales
   * @return Locale matched by localizable locales or null if no matches found
   */
  private static String localizableLocaleFromLocale(Locale locale, List<String> localizableLocales) {

    String result = findExactLocaleInList(localizableLocales, locale);
    if (result != null) {
      return result;
    }

    result = findExactLocaleCountryInList(localizableLocales, locale);
    if (result != null) {
      return result;
    }

    result = findLocaleCountryVariantInList(localizableLocales, locale);
    if (result != null) {
      return result;
    }

    return result;
  }


  /**
   * Find exact locale match for an array of locales.
   *
   * @param localeCodes List of locales to match eg: ["fr", "fr-CH"]
   * @param locale Locale to find in the list eg: "fr-CH"
   * @return locale tag or null eg: "fr-CH"
   */
  private static String findExactLocaleInList(List<String> localeCodes, Locale locale) {
    String localeCode = localeTag(locale);

    int index = localeCodes.indexOf(localeCode);
    if (index > 0) {
      return localeCodes.get(index);
    }
    return null;
  }

  /**
   * Find a match the closest parent language on a list.
   *
   * @param localeCodes List of locales to match eg: ["fr", "fr-CH"]
   * @param locale Locale to find in the list eg: "fr-TH"
   * @return locale tag or null eg: "fr"
   */
  private static String findExactLocaleCountryInList(List<String> localeCodes, Locale locale) {
    for (String localCode: localeCodes) {
      if (localCode.equalsIgnoreCase(locale.getLanguage())) {
        return localCode;
      }
    }
    return null;
  }

  /**
   * Find a base locale of country only in a list
   *
   * @param localeCodes List of locales to match eg: ["fr-CH", "fr-FR"]
   * @param locale Locale to find in the list eg: "fr"
   * @return locale tag or null eg: "fr-CH"
   */
  private static String findLocaleCountryVariantInList(List<String> localeCodes, Locale locale) {
    for (String localCode: localeCodes) {
      if (localCode.toLowerCase().startsWith(locale.getLanguage().toLowerCase())) {
        return localCode;
      }
    }
    return null;
  }

  private static String localeTag(Locale locale) {
    StringBuilder builder = new StringBuilder();
    builder.append(locale.getLanguage());
    if (locale.getCountry() != null && !locale.getCountry().isEmpty()) {
      builder.append("-").append(locale.getCountry());
    }
    if (locale.getVariant() != null && !locale.getVariant().isEmpty()) {
      builder.append("-").append(locale.getVariant());
    }
    return builder.toString();
  }

  private void saveToFile(Context context) {
    new FileLoader<SupportedLanguages>(context, fileName).store(this);
  }

  /**
   * Load SupportedLanguages class from file
   *
   * @param context Application context
   * @return Loaded class from file or a new instance with empty list of locales
   */
  private static SupportedLanguages loadStringsFromFile(Context context) {
    SupportedLanguages localFile = new FileLoader<SupportedLanguages>(context, fileName).loadFile();
    if (localFile == null) {
      localFile = new SupportedLanguages(context);
    }
    return localFile;
  }
}

abstract class SupportedLanguagesCallback extends JSONCallback {

  abstract void onDefaultLanguageChanged(List<String> languages);

  @Override
  public void onJSONResponse(Call call, JSONObject json) {
    List<String> languages = fromJSON(json);
    if (languages == null) {
      onFailure(call, null);
      return;
    }
    onDefaultLanguageChanged(languages);
  }

  private LinkedList<String> fromJSON(JSONObject json) {
    try {
      JSONArray languageCodes = json.getJSONArray("languages");
      LinkedList<String> strings = new LinkedList<>();
      for (int i = 0; i < languageCodes.length(); i++) {
        strings.add(languageCodes.getString(i));
      }
      return strings;
    } catch (Exception e) { return null; }

  }
}

abstract class SupportedLanguagesChangesCallback implements Serializable {
  void errorSyncing() { }
  abstract void onNoChangesDetected();
  abstract void onDefaultLanguageChanged(String newDefaultLanguageCode);
}