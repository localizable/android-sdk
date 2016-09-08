package io.localizable.sdk.networking.async;

import io.localizable.sdk.networking.HttpOperation;
import io.localizable.sdk.utils.LocalizableLog;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.Serializable;
import java.util.HashMap;


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
   * Get the current network client if no exists create one.
   *
   * @return shared instance of OkHttpClient
   */
  public OkHttpClient getNetworkClient() {
    if (networkClient == null) {
      networkClient = Network.getClient();
    }
    return networkClient;
  }


  /**
   * Creates a request with an HttpOperation.
   *
   * @param operation The operation to execute
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
        performGet(httpCallback);
        break;
      case POST:
        performPost(httpCallback);
        break;
      case PUT:
        performPut(httpCallback);
        break;
      default:
        break;
    }

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
  void performGet(Callback httpCallback) {
    Request.Builder builder = new Request.Builder();
    builder.url(operation.getUrl());
    applyHeaders(builder);
    this.getNetworkClient().newCall(builder.build()).enqueue(httpCallback);
  }


  /**
   * Performs an HttpPut to the specified url, will handle the response with the callback.
   *
   * @param httpCallback The Callback object.
   */
  void performPut(Callback httpCallback) {
    Request.Builder builder = new Request.Builder();
    builder.url(operation.getUrl());
    applyHeaders(builder);
    builder.put(RequestBody.create(MEDIA_TYPE_JSON, operation.getParameters()));
    this.getNetworkClient().newCall(builder.build()).enqueue(httpCallback);
  }


  /**
   * Performs an HttpPost to the specified url, will handle the response with the callback.
   *
   * @param httpCallback The Callback object.
   */
  void performPost(Callback httpCallback)  {
    Request.Builder builder = new Request.Builder();
    builder.url(operation.getUrl());
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
