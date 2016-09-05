package io.localizable.demo.sdk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.SparseArray;

import java.util.Locale;

import javax.annotation.Nonnull;

import io.localizable.demo.sdk.exceptions.NoSDKTokenFoundException;
import io.localizable.demo.sdk.exceptions.NoStringsClassFoundException;
import io.localizable.demo.sdk.model.Language;
import io.localizable.demo.sdk.model.UserDefinedLocale;
import io.localizable.demo.sdk.networking.async.Network;
import io.localizable.demo.sdk.utils.StringClassLoader;
import io.localizable.demo.sdk.utils.ManifestUtils;

public class Localizable {

  public static class Constants {
    public static String DEFAULT_LANGUAGE_CODE = "base";
  }

  private static Language language;
  private static SparseArray<String> appStrings;
  private static Context context;
  private static String apiToken;

  public static void setup(@Nonnull Context context) {
    try {
      Network.Setup(Network.NetworkType.NETWORK);
      Localizable.apiToken = ManifestUtils.getAPITokenFromMetadata(context);
      Localizable.context = context;
      Localizable.appStrings = StringClassLoader.loadStringsFromContext(context);
      Localizable.language = new Language(Localizable.appStrings, context, apiToken);
    } catch (NoSDKTokenFoundException | NoStringsClassFoundException | PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Set the main locale of the App. Sets the locale in the parameter as the top priority on finding
   * the Localizable supported languages resolution
   *
   * @param locale Locale to set as top priority
   */
  public static void setLocale(Locale locale) {
    UserDefinedLocale.setUserLocale(context, locale);
    Localizable.language = new Language(Localizable.appStrings, context, apiToken);
  }


  public static String getString(int stringID, Object... parameters) {
    return language.getString(context, stringID, parameters);
  }

  public static String getString(int stringID) {
    return language.getString(context, stringID);
  }
}
