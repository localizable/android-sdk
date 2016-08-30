package io.localizable.demo.sdk.networking;

import java.util.HashMap;

import io.localizable.demo.sdk.networking.async.HttpRequest;

public class LocalizableOperation extends HttpOperation {

  @Override
  protected String getBaseEndpoint() {
    return "http://192.168.0.149:4000/api/v1/";
  }

  protected LocalizableOperation(String path, HashMap<String, String> parameters,
                                 HttpRequest.NetworkOperationType requestType, String apiToken) {
    super(path, parameters, requestType, defaultLocalizableHeadersWithToken(apiToken));
  }

  public static HttpOperation UpdateLanguage(String code, long lastModified, String apiToken) {
    HashMap<String, String> parameters = new HashMap<>();
    parameters.put("modified_at", "" + lastModified);

    return new LocalizableOperation("languages/"+code, parameters, HttpRequest.NetworkOperationType.GET, apiToken);
  }

  private static HashMap<String, String> defaultLocalizableHeadersWithToken(String apiToken) {
    HashMap<String, String> headers = defaultLocalizableHeaders();
    headers.put("X-LOCALIZABLE-TOKEN", apiToken);
    return headers;
  }

  private static HashMap<String, String> defaultLocalizableHeaders() {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("Accept", "application/json");
    return headers;
  }
}
