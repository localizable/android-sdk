package io.localizable.sdk.exceptions;

/**
 * NoSdkTokenFoundException is thrown when the SDK token is not found on the manifest file.
 */
public class UninitializedSdkException extends LocalizableException {

  /**
   * Default constructor.
   */
  public UninitializedSdkException() {
    super("Localizable sdk was not setup, please call Localizable.setup(application).");
  }
}
