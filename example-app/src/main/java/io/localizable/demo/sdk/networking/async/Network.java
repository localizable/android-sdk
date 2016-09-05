package io.localizable.demo.sdk.networking.async;

import java.util.concurrent.TimeUnit;

import io.localizable.demo.sdk.networking.client.LocalizableMockClient;
import okhttp3.OkHttpClient;


/**
 * Network requests client generator
 */
public class Network {

  /**
   * Current OKHttpClient
   */
  private static OkHttpClient client;

  // Constants
  private static final int TASK_TIMEOUT = 15; // seconds

  /**
   * Get the current OKHttpClient
   *
   * @return The current OKHttpClient
   */
  public static OkHttpClient getClient() {
    return client;
  }

  /**
   * Sets up the environment as Network
   */
  public static void Setup() {
    Network.Setup(NetworkType.NETWORK);
  }


  /**
   * Sets Up the OKHttpClient with a given environment
   *
   * @param buildType Selected Environment
   */
  public static void Setup(NetworkType buildType) {
    switch (buildType) {
      case MOCK:
        Network.client = new LocalizableMockClient();
        break;
      case NETWORK:
      Network.client = new OkHttpClient.Builder()
          .connectTimeout(TASK_TIMEOUT, TimeUnit.SECONDS)
          .build();
        break;
    }
  }

  /**
   * Network types
   *
   */
  public enum NetworkType {
    /**
     * Only mocked responses
     */
    MOCK,

    /**
     * Network request
     */
    NETWORK
  }
}
