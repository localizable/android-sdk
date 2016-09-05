package io.localizable.demo.sdk.networking.async;

import java.io.Serializable;
import java.util.HashMap;

import io.localizable.demo.sdk.networking.HttpOperation;
import io.localizable.demo.sdk.utils.LocalizableLog;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * HttpRequest is a savable model around OkHttpRequests.
 * It contains an operation with all the required parameters and headers
 */
public class HttpRequest implements Serializable {

  public static final MediaType MEDIA_TYPE_JSON
      = MediaType.parse("application/json; charset=utf-8");

  // Properties
  HttpOperation operation;
  OkHttpClient networkClient;

  /**
   * Creates a request with an HttpOperation
   *
   * @param operation     The operation to execute
   */
  public HttpRequest(HttpOperation operation) {
    this.operation = operation;
  }

  /**
   * Executes the HTTPRequest with the operation and returns the result thought the callback.
   *
   * @param httpCallback The Callback object.
   */
  public void execute(final Callback httpCallback) {
    LocalizableLog.debug("Executing request: " + operation.toString());
    switch (operation.getRequestType()) {
      case GET:
        performGET(httpCallback);
        break;
      case POST:
        performPOST(httpCallback);
        break;
      case PUT:
        performPUT(httpCallback);
        break;
      default:
        break;
    }

  }

  public OkHttpClient getNetworkClient() {
    if (networkClient == null)
      networkClient = Network.getClient();
    return networkClient;
  }

  /**
   * Adds the Operation parameters to the request builder.
   *
   * @param builder The RequestBuilder to add the headers
   */
  private void applyHeaders(Request.Builder builder) {
    HashMap<String, String> headers = operation.getHeaders();
    for (String headerKey : headers.keySet()) {
      builder.addHeader(headerKey, headers.get(headerKey));
    }
  }

  /**
   * Performs an HttpGet to the specified url, will handle the response with the callback.
   *
   * @param httpCallback The Callback object.
   */
  void performGET(Callback httpCallback) {
    Request.Builder builder = new Request.Builder();
    builder.url(operation.getURL());
    applyHeaders(builder);
    this.getNetworkClient().newCall(builder.build()).enqueue(httpCallback);
  }

  /**
   * Performs an HttpPut to the specified url, will handle the response with the callback.
   *
   * @param httpCallback The Callback object.
   */
  void performPUT(Callback httpCallback) {
    Request.Builder builder = new Request.Builder();
    builder.url(operation.getURL());
    applyHeaders(builder);
    builder.put(RequestBody.create(MEDIA_TYPE_JSON, operation.getParameters()));
    this.getNetworkClient().newCall(builder.build()).enqueue(httpCallback);
  }

  /**
   * Performs an HttpPost to the specified url, will handle the response with the callback.
   *
   * @param httpCallback The Callback object.
   */
  void performPOST(Callback httpCallback)  {
    Request.Builder builder = new Request.Builder();
    builder.url(operation.getURL());
    applyHeaders(builder);
    builder.post(RequestBody.create(MEDIA_TYPE_JSON, operation.getParameters()));
    this.getNetworkClient().newCall(builder.build()).enqueue(httpCallback);
  }

  // Type Enum
  /**
   * The possible network operation types: GET, POST and PUT.
   * Serializable for saving HttpRequests to file.
   */
  public enum NetworkOperationType implements Serializable {
    GET, POST, PUT
  }
}
