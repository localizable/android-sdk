package io.localizable.sdk.exceptions;

/**
 * NoSdkTokenFoundException is thrown when the SDK token is not found on the manifest file.
 */
public class NetworkException extends Exception {

  /**
   * Default constructor.
   */
  public NetworkException(String message) {
    super("Error contacting server " + message);
  }
}
