package io.localizable.sdk.networking;

import io.localizable.sdk.networking.async.HttpRequest;

import java.util.HashMap;

/**
 * Localizable implementation of HttpOperations,
 * Contains all the Localizable Operation generators.
 */
public class LocalizableOperation extends HttpOperation {



  /**
   * Localizable base endpoint.
   *
   * @return Localizable base endpoint
   */
  @Override
  protected final String getBaseEndpoint() {
    return "https://localizable-api.herokuapp.com";
  }

  /**
   * Localizable base endpoint.
   *
   * @return Localizable base endpoint
   */
  @Override
  protected final String getBaseEndpointPath() {
    return getBaseEndpoint() + "/api/v1/";
  }

  /**
   * Constructs an HttpOperation.
   *
   * @param path - Operation path
   * @param parameters - Operation request parameters
   * @param requestType - Operation request type GET PUT POST
   * @param apiToken - Localizable api token
   */
  protected LocalizableOperation(String path, HashMap<String, String> parameters,
                                 HttpRequest.NetworkOperationType requestType, String apiToken) {
    super(path, parameters, requestType, defaultLocalizableHeadersWithToken(apiToken));
  }

  /**
   * Constructs the operation headers given a API token.
   *
   * @param apiToken - Current Localizable API token
   * @return Operation headers with the API token
   */
  private static HashMap<String, String> defaultLocalizableHeadersWithToken(String apiToken) {
    HashMap<String, String> headers = defaultLocalizableHeaders();
    headers.put("X-LOCALIZABLE-TOKEN", apiToken);
    return headers;
  }

  /**
   * Default Localizable operation headers.
   * <br/>
   * Content-Type + Accept
   *
   * @return Default Localizable operation headers
   */
  private static HashMap<String, String> defaultLocalizableHeaders() {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("Accept", "application/json");
    return headers;
  }

  // Localizable operation generators start Here

  /**
   * Fetch the most recent language tokens for a language, since last modified.
   *
   * @param code Language code for the language to fetch
   * @param lastModified Timestamp of the last time updated
   * @param apiToken Localizable API Token
   * @return Http operation to fetch the language token updates
   */
  public static HttpOperation updateLanguage(String code, long lastModified, String apiToken) {
    HashMap<String, String> parameters = new HashMap<>();
    parameters.put("modified_at", "" + lastModified);

    return new LocalizableOperation("languages/" + code, parameters,
        HttpRequest.NetworkOperationType.GET, apiToken);
  }

  /**
   * Fetch all the supported language codes.
   *
   * @param apiToken Localizable API Token
   * @return Http Operation to fetch the Language codes
   */
  public static HttpOperation languageCodes(String apiToken) {
    return new LocalizableOperation("languages/platforms/android", new HashMap<String, String>(),
        HttpRequest.NetworkOperationType.GET, apiToken);
  }

}
