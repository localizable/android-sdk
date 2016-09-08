package io.localizable.sdk.networking.client;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Mock client to return mocked responses.
 */
public class LocalizableMockClient extends OkHttpClient {

  /**
   * Default constructor - Loads the mocked responses to ensure a response is given.
   */
  public LocalizableMockClient() {
    LocalizableMockResponseHolder.loadDefaultResponses();
  }

  /**
   * Call the request.
   *
   * @param request Request to be executed
   * @return LocalizableMockCall for the request
   */
  @Override
  public Call newCall(Request request) {
    return new LocalizableMockCall(request);
  }
}