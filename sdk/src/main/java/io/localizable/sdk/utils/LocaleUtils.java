package io.localizable.sdk.utils;

import android.content.Context;
import android.os.Build;
import android.os.LocaleList;

import io.localizable.sdk.Localizable;
import io.localizable.sdk.model.UserDefinedLocale;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class LocaleUtils {

  /**
   * Returns the Localizable locale to use given a list of Localizable locales, checking devices
   * user language settings and localizable user set language.
   *
   * @param languages Localizable language list, it may contain the "base" language
   * @param context Application context
   * @return Localizable locale tag from the available languages, or null if none match
   */
  public static String selectDefaultLanguageCode(List<String> languages, Context context) {
    return localizableDefaultLocale(deviceLocales(context), languages);
  }

  /**
   * Checks if the new server code list is equal to the current cached list,
   * important: order could be different.
   *
   * @param list1 list of locale tags
   * @param list2 list of locale tags
   * @return comparison result of the lists
   */
  public static boolean listOfLanguageCodesEquals(List<String> list1, List<String> list2) {

    if (list1.size() != list2.size()) {
      return false;
    }

    for (String code : list1) {
      if (!list2.contains(code)) {
        return false;
      }
    }
    return true;
  }


  /**
   * Returns the Localizable locale to use given a list of Localizable locales and device locales.
   *
   * @param deviceLocales List of in device locales list
   * @param localizableLocales List of localizable locales
   * @return Localizable locale tag, or null if none match
   */
  private static String localizableDefaultLocale(List<Locale> deviceLocales,
                                                 List<String> localizableLocales) {
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
   * Return the list of device locales (See android 7+ locales), ordered by priority.
   * If the user explicitly set the Localizable language that language is the first one in order
   *
   * @param context Application context
   * @return List of device locales by order of priority and if a language was set by user
   *     that one is the first
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
      //noinspection deprecation
      locales.add(context.getResources().getConfiguration().locale);
    }
    return locales;
  }


  /**
   * Find a match for a locale in a list of locales, using google android 7.0 fallback system.
   * https://developer.android.com/guide/topics/resources/multilingual-support.html
   *
   * @param locale Locale to match
   * @param localizableLocales Possible list of localizable locales
   * @return Locale matched by localizable locales or null if no matches found
   */
  private static String localizableLocaleFromLocale(Locale locale,
                                                    List<String> localizableLocales) {

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
    return null;
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
   * Find a base locale of country only in a list.
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

  /**
   * Returns a locale from localizable tag.
   * @param tag Localizable language tag
   * @return Locale from tag
   */
  public static Locale localeFromTag(String tag) {
    if (tag.equalsIgnoreCase(Localizable.Constants.LOCALIZABLE_DEFAULT_LANGUAGE_CODE)) {
      return null;
    }
    String[] localeParts = tag.split("-");
    switch (localeParts.length) {
      case 1:
        return new Locale(localeParts[0]);
      case 2:
        return new Locale(localeParts[0], localeParts[1]);
      case 3:
        return new Locale(localeParts[0], localeParts[1], localeParts[2]);
      default:
        return null;
    }
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
}
