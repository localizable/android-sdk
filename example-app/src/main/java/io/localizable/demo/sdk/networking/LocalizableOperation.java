package io.localizable.demo.sdk.networking;

import java.util.HashMap;

import io.localizable.demo.sdk.networking.async.HttpRequest;

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
    return "https://localizable-api.herokuapp.com/api/v1/";
  }
  /*protected String getBaseEndpoint() {
    return "http://192.168.0.149:4000/api/v1/";
  }*/


  /**
   * Constructs an HttpOperation.
   *
   * @param path - Operation path
   * @param parameters - Operation request parameters
   * @param requestType - Operation request type GET PUT POST
   * @param apiToken - Localizable api token
   */
  protected LocalizableOperation(final String path, final HashMap<String, String> parameters,
                                 final HttpRequest.NetworkOperationType requestType, final String apiToken) {
    super(path, parameters, requestType, defaultLocalizableHeadersWithToken(apiToken));
  }

  /**
   * Constructs the operation headers given a API token.
   *
   * @param apiToken - Current Localizable API token
   * @return Operation headers with the API token
   */
  private static HashMap<String, String> defaultLocalizableHeadersWithToken(final String apiToken) {
    HashMap<String, String> headers = defaultLocalizableHeaders();
    headers.put("X-LOCALIZABLE-TOKEN", apiToken);
    return headers;
  }

  /**
   * Default Localizable operation headers.
   *
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
  public static HttpOperation updateLanguage(final String code, final long lastModified, final String apiToken) {
    HashMap<String, String> parameters = new HashMap<>();
    parameters.put("modified_at", "" + lastModified);

    return new LocalizableOperation("languages/"+ code, parameters,
        HttpRequest.NetworkOperationType.GET, apiToken);
  }

  /**
   * Fetch all the supported language codes.
   *
   * @param apiToken Localizable API Token
   * @return Http Operation to fetch the Language codes
   */
  public static HttpOperation languageCodes(final String apiToken) {
    return new LocalizableOperation("languages/platforms/android", new HashMap<String, String>(),
        HttpRequest.NetworkOperationType.GET, apiToken);
  }

}
