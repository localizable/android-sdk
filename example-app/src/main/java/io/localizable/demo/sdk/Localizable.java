package io.localizable.demo.sdk;

import android.content.Context;
import android.util.SparseArray;

import java.util.Locale;

import javax.annotation.Nonnull;

import io.localizable.demo.sdk.exceptions.NoSDKTokenFoundException;
import io.localizable.demo.sdk.exceptions.NoStringsClassFoundException;
import io.localizable.demo.sdk.model.LocalizableLanguage;
import io.localizable.demo.sdk.networking.async.Network;
import io.localizable.demo.sdk.utils.LocalizableLog;
import io.localizable.demo.sdk.utils.StringClassLoader;
import io.localizable.demo.sdk.utils.ManifestUtils;

public class Localizable {

  private static LocalizableLanguage localizableLanguage;
  private static SparseArray<String> appStrings;
  private static Context context;
  private static String apiToken;
  private static String currentLanguageCode;

  public static void setup(@Nonnull Context context) {
    try {
      Network.Setup(Network.NetworkType.TEST);
      Localizable.apiToken = ManifestUtils.getAPITokenFromMetadata(context);
      Localizable.context = context;
      Localizable.appStrings = StringClassLoader.loadStringsFromContext(context);
      Localizable.localizableLanguage = new LocalizableLanguage(Localizable.appStrings, context, apiToken);
      Localizable.getCurrentLanguageCode();
    } catch (NoSDKTokenFoundException | NoStringsClassFoundException e) {
      e.printStackTrace();
    }
  }

  public static String getString(int stringID) {
    return localizableLanguage.getString(context, stringID);
  }

  private static String getCurrentLanguageCode() {
    if (currentLanguageCode != null)
      return currentLanguageCode;

    if (context == null)
      return null;

    LocalizableLog.debug("Language -> " + Locale.getDefault().getLanguage());

    LocalizableLog.debug("Context code ->" + context.getResources().getConfiguration().locale.getLanguage());

    return "base";
  }

}
