package io.localizable.sdk.networking.client;

import android.content.Context;

import io.localizable.sdk.networking.LocalizableOperation;
import io.localizable.sdk.networking.async.Network;

public class NetworkMocker {

  /**
   * Mock response of default request for en language.
   *
   * @param context Application context
   */
  public static void setupMockedResponsesBaseStrings(Context context) {
    Network.setup(Network.NetworkType.MOCK);
    LocalizableMockResponseHolder.addResponseForOperation("en",
        LocalizableOperation.updateLanguage("en", 0, "token"), context);

    LocalizableMockResponseHolder.addResponseForOperation("supported_languages",
        LocalizableOperation.languageCodes("token"), context);

  }

  /**
   * Mock add mocked response for dashboardUpdate.
   *
   * @param context Application context
   */
  public static void setupMockedResponseForEnUpdate(Context context) {
    Network.setup(Network.NetworkType.MOCK);
    LocalizableMockResponseHolder.addResponseForOperation("en_updates",
        LocalizableOperation.updateLanguage("en", 0, "token"), context);
  }
}
