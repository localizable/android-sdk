package io.localizable.demo.sdk.networking;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import io.localizable.demo.sdk.exceptions.MethodNotImplementedException;
import io.localizable.demo.sdk.networking.async.HttpRequest;
import io.localizable.demo.sdk.utils.LocalizableLog;

public class HttpOperation {

  protected String getBaseEndpoint() {
    LocalizableLog.error("Base endpoint not implemented");
    return null;
  }

  protected String getUserAgent() {
    LocalizableLog.error("GET User-Agent not implemented");
    return null;
  }

  private String path;
  private HashMap<String, String> parameters;
  private HttpRequest.NetworkOperationType requestType;
  private HashMap<String, String> headers;

  protected HttpOperation(String path, HashMap<String, String> parameters,
                          HttpRequest.NetworkOperationType requestType) {
    this(path, parameters, requestType, null);
  }

  protected HttpOperation(String path, HashMap<String, String> parameters,
                          HttpRequest.NetworkOperationType requestType, HashMap<String, String> headers) {
    this.path = path;
    this.parameters = parameters;
    this.requestType = requestType;

    this.headers = new HashMap<>();
    if (getUserAgent() != null)
      this.headers.put("UserAgent", getUserAgent());

    if (headers != null) {
      this.headers.putAll(headers);
    }
  }

  public URL getURL() {
    try {
      StringBuilder completeURLBuilder = new StringBuilder();
      completeURLBuilder.append(getBaseEndpoint());
      completeURLBuilder.append(path);
      if (requestType == HttpRequest.NetworkOperationType.GET && parameters != null) {
        boolean first = true;
        for (String key : parameters.keySet()) {
          if (first) {
            first = false;
            completeURLBuilder.append("?");
          } else {
            completeURLBuilder.append("&");
          }
          completeURLBuilder.append(key).append("=").append(parameters.get(key));
        }
      }
      return new URL(completeURLBuilder.toString());
    } catch (MalformedURLException e) {
      return null;
    }
  }

  public String getParameters() {
    if (parameters == null)
      return null;

    return new JSONObject(parameters).toString();
  }

  public HttpRequest.NetworkOperationType getRequestType() {
    return requestType;
  }

  public HashMap<String, String> getHeaders() {
    return headers;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("curl");
    switch (getRequestType()) {
      case POST:
        builder.append(" -X POST");
      case PUT:
        builder.append(" -X PUT");
      default:
        break;
    }
    if (getHeaders() != null && !getHeaders().isEmpty()) {
      for (String key : getHeaders().keySet()) {
        builder.append(" -H")
            .append(" \"")
            .append(key).append(":").append(getHeaders().get(key))
            .append("\"");
      }
    }
    if (getParameters() != null && getRequestType() != HttpRequest.NetworkOperationType.GET) {
      builder.append(" -d ").append(getParameters());
    }
    builder.append(" ").append(getURL().toString());
    return builder.toString();

  }
}

