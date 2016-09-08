package io.localizable.sdk.networking;

import io.localizable.sdk.networking.async.HttpRequest;
import io.localizable.sdk.utils.LocalizableLog;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


/**
 * Base HttpOperation.
 * <br/>
 * Contains all components of a Http request
 * <br/>
 * Headers - Custom headers and customizable user-agent
 * <br/>
 * RequestType - Only GET, PUT or POST supported for now
 * <br/>
 * Parameters - Automatic add parameters to URL if is a GET or send them as Body if the request is
 * a POST or PUT
 * <br/>
 * Path - path to append to the base endpoint
 */

public abstract class HttpOperation {


  /**
   * Endpoint to append the path and parameters.
   *
   * @return The base endpoint to the request
   */
  protected abstract String getBaseEndpoint();


  /**
   * User agent to the Request.
   *
   * @return The user agent or null
   */
  protected String getUserAgent() {
    LocalizableLog.error("GET User-Agent not implemented");
    return null;
  }


  private String path;
  private HashMap<String, String> parameters;
  private HttpRequest.NetworkOperationType requestType;
  private HashMap<String, String> headers;


  /**
   * Constructs the operation without headers.
   *
   * @param path Path to the request
   * @param parameters Request parameters
   * @param requestType Request type - GET/POST/PUT
   */
  @SuppressWarnings("unused")
  protected HttpOperation(String path, HashMap<String, String> parameters,
                          HttpRequest.NetworkOperationType requestType) {
    this(path, parameters, requestType, null);
  }


  /**
   * Constructs the operation.
   *
   * @param path Path to the request
   * @param parameters Request parameters
   * @param requestType Request type - GET/POST/PUT
   * @param headers Headers to add to the request - User-Agent not included
   */
  protected HttpOperation(String path, HashMap<String, String> parameters,
                          HttpRequest.NetworkOperationType requestType,
                          HashMap<String, String> headers) {
    this.path = path;
    this.parameters = parameters;
    this.requestType = requestType;

    this.headers = new HashMap<>();
    if (getUserAgent() != null) {
      this.headers.put("UserAgent", getUserAgent());
    }

    if (headers != null) {
      this.headers.putAll(headers);
    }
  }


  /**
   * Complete url to the operation.
   *
   * @return Complete operation URL or null if the generated URL is invalid
   */
  public URL getUrl() {
    try {
      StringBuilder completeUrlBuilder = new StringBuilder();
      completeUrlBuilder.append(getBaseEndpoint());
      completeUrlBuilder.append(path);
      if (requestType == HttpRequest.NetworkOperationType.GET && parameters != null) {
        boolean first = true;
        for (String key : parameters.keySet()) {
          if (first) {
            first = false;
            completeUrlBuilder.append("?");
          } else {
            completeUrlBuilder.append("&");
          }
          completeUrlBuilder.append(key).append("=").append(parameters.get(key));
        }
      }
      return new URL(completeUrlBuilder.toString());
    } catch (MalformedURLException exception) {
      LocalizableLog.error(exception);
      return null;
    }
  }


  /**
   * Operation parameters as JSON String.
   *
   * @return Operation parameters as JSONString or null if no parameters found
   */
  public String getParameters() {
    if (parameters == null) {
      return null;
    }
    return new JSONObject(parameters).toString();
  }


  /**
   *  Network request type.
   *
   * @return GET PUT POST
   */
  public HttpRequest.NetworkOperationType getRequestType() {
    return requestType;
  }


  /**
   * Operation headers.
   *
   * @return returns the operation headers including defined user agent
   */
  public HashMap<String, String> getHeaders() {
    return headers;
  }

  /**
   * String representation of the Operation - currently showing request as a Curl command.
   *
   * @return String representation of the Request
   */
  @Override
  public String toString() {
    return curlRepresentation();
  }


  /**
   * Curl representation of the Operation.
   *
   * @return the Curl command to run the request
   */
  private String curlRepresentation() {
    StringBuilder builder = new StringBuilder();
    builder.append("curl");
    switch (getRequestType()) {
      case POST:
        builder.append(" -X POST");
        break;
      case PUT:
        builder.append(" -X PUT");
        break;
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
    builder.append(" ").append(getUrl().toString());
    return builder.toString();
  }
}

