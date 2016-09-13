package io.localizable.sdk;

import android.annotation.TargetApi;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.SparseArray;

import io.localizable.sdk.exceptions.UninitializedSdkException;
import io.localizable.sdk.model.Language;
import io.localizable.sdk.model.SupportedLanguages;
import io.localizable.sdk.model.UserDefinedLocale;
import io.localizable.sdk.utils.LocalizableLog;
import io.localizable.sdk.utils.ManifestUtils;
import io.localizable.sdk.utils.StringClassLoader;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Localizable singleton class.
 */
@SuppressWarnings("unused")
public final class Localizable {

  public static final class Constants {
    /**
     * When none of the localizable languages match the values folder is represented as base.
     */
    public static final String LOCALIZABLE_DEFAULT_LANGUAGE_CODE = "default";
  }

  private static Localizable instance;

  /**
   * Contains the localizable string translations for the current Locale.
   */
  private Language language;

  /**
   * Application string resource keys and R int values.
   */
  private SparseArray<String> appStrings;

  /**
   * Application context.
   */
  private Context applicationContext;

  /**
   * Localizable API token.
   */
  private String apiToken;


  private Localizable(@Nonnull Context context) {
    this.apiToken = ManifestUtils.getApiTokenFromMetadata(context);
    this.applicationContext = context;
    this.appStrings = StringClassLoader.loadStringsFromContext(context);
    this.language = Language.loadLanguage(appStrings, context, apiToken);
  }

  /**
   * Initialize and Setup the Localizable SDK.
   *
   * @param context Application context
   */
  public static void setup(@Nonnull Context context) {
    instance = new Localizable(context);
  }


  /**
   * Get localized resource with identifier.
   *
   * @param stringId String resource identifier
   * @return Localized String from localizable backend or app resources
   */
  public static String getString(int stringId) {
    if (instance == null) {
      throw new UninitializedSdkException();
    }
    return instance.language.getString(instance.applicationContext, stringId);
  }


  /**
   * Get localized resource with identifier and parameters.
   *
   * @param stringId String resource identifier
   * @param parameters String parameters
   * @return Localized String from localizable backend or app resources
   */
  public static String getString(int stringId, Object... parameters) {
    if (instance == null) {
      throw new UninitializedSdkException();
    }
    return instance.language.getString(instance.applicationContext, stringId, parameters);
  }


  /**
   * Sets the localizable and application main locale.
   *
   * @param locale Locale to set as top priority or null to reset to default locale
   */
  public static void setLocale(@Nullable Locale locale) {
    setLocale(locale, true);
  }


  /**
   * Sets the localizable and application main locale, if shouldSetTheApplicationContext is false
   * only sets the localizable main Locale.
   *
   * @param locale Locale to set as top priority or null to reset to default locale
   * @param shouldSetTheApplicationContext true if the Application context should change too
   */
  public static void setLocale(@Nullable Locale locale, boolean shouldSetTheApplicationContext) {
    if (instance == null) {
      throw new UninitializedSdkException();
    }

    UserDefinedLocale.setUserLocale(instance.applicationContext, locale);
    if (shouldSetTheApplicationContext) {
      setApplicationContextLocale(instance.applicationContext, locale);
    }
    instance.language = Language.loadLanguage(instance.appStrings, instance.applicationContext,
        instance.apiToken);
  }

  /**
   * Returns the list of available Localizable locales.
   *
   * @return List of supported locales.
   */
  public static List<Locale> availableLocales() {
    if (instance == null) {
      throw new UninitializedSdkException();
    }

    return SupportedLanguages.cachedSupportedLanguages(instance.applicationContext);
  }

  /**
   * Set the main locale for the Application Context.
   *
   * @param context Application context
   * @param locale Locale to set
   */
  static void setApplicationContextLocale(@Nonnull Context context, @Nullable Locale locale) {
    Resources resources = context.getResources();
    Configuration configuration = resources.getConfiguration();
    Locale defaultDeviceLocale = getDeviceDefaultLocale(context);

    if (locale == null) {
      LocalizableLog.error("trying to set a null locale as the main device locale.");
      return;
    }

    if (!defaultDeviceLocale.equals(locale)) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        setConfigLocaleNougat(configuration, locale);
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        setConfigLocale(configuration, locale);
      } else {
        setConfigLocaleCompat(configuration, locale);
      }
      resources.updateConfiguration(configuration, null);
    }
  }

  /**
   * Sets the configuration locale using JELLY_BEAN_MR1 > Api version call.
   *
   * @param configuration resources configuration
   * @param locale new locale
   */
  @TargetApi(Build.VERSION_CODES.N)
  private static void setConfigLocaleNougat(Configuration configuration, Locale locale) {
    LocaleList localeList = configuration.getLocales();
    List<Locale> locales = new LinkedList<>();
    locales.add(locale);
    for (int i = 0; i < localeList.size(); i++) {
      if (!locales.contains(localeList.get(i))) {
        locales.add(localeList.get(i));
      }
    }

    Locale[] localeArray = new Locale[locales.size()];
    for (int i = 0; i < locales.size(); i++) {
      localeArray[i] = locales.get(i);
    }
    configuration.setLocales(new LocaleList(localeArray));
  }

  /**
   * Sets the configuration locale using JELLY_BEAN_MR1 > Api version call.
   *
   * @param configuration resources configuration
   * @param locale new locale
   */
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  private static void setConfigLocale(Configuration configuration, Locale locale) {
    configuration.setLocale(locale);
  }

  /**
   * Sets the configuration locale using JELLY_BEAN_MR1 older versions call.
   *
   * @param configuration resources configuration
   * @param locale new locale
   */
  @SuppressWarnings("deprecation")
  private static void setConfigLocaleCompat(Configuration configuration, Locale locale) {
    configuration.locale = locale;
  }


  /**
   * Get the main locale defined for user in the device.
   *
   * @param context Application context
   * @return Main device Locale
   */
  static Locale getDeviceDefaultLocale(@Nonnull final Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      return context.getResources().getConfiguration().getLocales().get(0);
    } else {
      //noinspection deprecation
      return context.getResources().getConfiguration().locale;
    }
  }
}
