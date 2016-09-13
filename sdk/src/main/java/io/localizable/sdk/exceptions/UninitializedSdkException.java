package io.localizable.sdk.exceptions;

/**
 * UninitializedSdkException is thrown when the sdk is used without being initialized first.
 */
public class UninitializedSdkException extends LocalizableException {

  /**
   * Default constructor.
   */
  public UninitializedSdkException() {
    super("Localizable sdk was not setup, please call Localizable.setup(application).");
  }
}
