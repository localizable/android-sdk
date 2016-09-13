package io.localizable.sdk.exceptions;

/**
 * Network Exception is thrown when the SDK finds encounters a network error.
 */
public class NetworkException extends Exception {

  /**
   * Default constructor.
   *
   * @param message exception message
   */
  public NetworkException(String message) {
    super("Error contacting server " + message);
  }
}
