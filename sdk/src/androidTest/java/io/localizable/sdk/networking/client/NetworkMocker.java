package io.localizable.sdk.networking.client;

import android.content.Context;

import io.localizable.sdk.Localizable;
import io.localizable.sdk.networking.HttpOperation;
import io.localizable.sdk.networking.LocalizableOperation;
import io.localizable.sdk.networking.async.Network;

public class NetworkMocker {


  public static void addResponseForOperation(String fileName, HttpOperation operation, Context context) {
    LocalizableMockResponseHolder.addResponseForOperation(fileName, operation, context);
  }

  /**
   * Mock response of default request for en language.
   *
   * @param context Application context
   */
  public static void setupMockedResponsesBaseStrings(Context context) {
    NetworkMocker.clearMockedResponses();

    LocalizableMockResponseHolder.addResponseForOperation("en",
        LocalizableOperation.updateLanguage("en", 0, "token"), context);

    LocalizableMockResponseHolder.addResponseForOperation("supported_languages",
        LocalizableOperation.languageCodes("token"), context);

  }

  public static void clearMockedResponses() {
    Network.setup(Network.NetworkType.MOCK);
    LocalizableMockResponseHolder.clear();
  }

  /**
   * Mock add mocked response for dashboardUpdate.
   *
   * @param context Application context
   */
  public static void setupMockedResponseForEnUpdate(Context context) {
    NetworkMocker.clearMockedResponses();

    LocalizableMockResponseHolder.addResponseForOperation("supported_languages",
        LocalizableOperation.languageCodes("token"), context);

    LocalizableMockResponseHolder.addResponseForOperation("en_updates",
        LocalizableOperation.updateLanguage("en", 0, "token"), context);
  }

  /**
   * Mock add mocked response for dashboardUpdate.
   *
   * @param context Application context
   */
  public static void setupMockedResponseForPtBr(Context context) {
    NetworkMocker.clearMockedResponses();

    LocalizableMockResponseHolder.addResponseForOperation("supported_languages",
        LocalizableOperation.languageCodes("token"), context);

    LocalizableMockResponseHolder.addResponseForOperation("pt_br",
        LocalizableOperation.updateLanguage("pt-BR", 0, "token"), context);
  }
}
