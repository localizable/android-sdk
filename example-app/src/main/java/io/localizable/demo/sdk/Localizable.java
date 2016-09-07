package io.localizable.demo.sdk;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.SparseArray;

import io.localizable.demo.sdk.model.Language;
import io.localizable.demo.sdk.model.UserDefinedLocale;
import io.localizable.demo.sdk.networking.async.Network;
import io.localizable.demo.sdk.utils.ManifestUtils;
import io.localizable.demo.sdk.utils.StringClassLoader;

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
    public static final String LOCALIZABLE_DEFAULT_LANGUAGE_CODE = "base";
  }

  /**
   * Contains the localizable string translations for the current Locale.
   */
  private static Language language;

  /**
   * Application string resource keys and R int values.
   */
  private static SparseArray<String> appStrings;

  /**
   * Application context.
   */
  private static Context applicationContext;

  /**
   * Localizable API token.
   */
  private static String apiToken;

  /**
   * Initialize and Setup the Localizable SDK.
   *
   * @param context Application context
   */
  public static void setup(@Nonnull Context context) {
    Network.setup();
    Localizable.apiToken = ManifestUtils.getApiTokenFromMetadata(context);
    Localizable.applicationContext = context;
    Localizable.appStrings = StringClassLoader.loadStringsFromContext(context);
    Localizable.language = new Language(Localizable.appStrings, context, apiToken);
  }


  /**
   * Get localized resource with identifier.
   *
   * @param stringId String resource identifier
   * @return Localized String from localizable backend or app resources
   */
  public static String getString(int stringId) {
    return language.getString(applicationContext, stringId);
  }


  /**
   * Get localized resource with identifier and parameters.
   *
   * @param stringId String resource identifier
   * @param parameters String parameters
   * @return Localized String from localizable backend or app resources
   */
  public static String getString(int stringId, Object... parameters) {
    return language.getString(applicationContext, stringId, parameters);
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
    UserDefinedLocale.setUserLocale(applicationContext, locale);
    if (shouldSetTheApplicationContext) {
      setApplicationContextLocale(applicationContext, locale);
    }
    Localizable.language = new Language(Localizable.appStrings, applicationContext, apiToken);
  }


  /**
   * Set the main locale for the Application Context, if the locale is null sets the locale as the
   * current primary language.
   *
   * @param context Application context
   * @param locale Locale to set
   */
  static void setApplicationContextLocale(@Nonnull Context context, @Nullable Locale locale) {
    Resources resources = context.getResources();
    Configuration configuration = resources.getConfiguration();
    Locale defaultDeviceLocale = getDeviceDefaultLocale(context);
    Locale newLocale = locale;

    if (newLocale == null) {
      newLocale = defaultDeviceLocale;
    }

    if (!defaultDeviceLocale.equals(newLocale)) {
      configuration.setLocale(newLocale);
      resources.updateConfiguration(configuration, null);
    }
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
