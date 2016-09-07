package io.localizable.demo.sdk.exceptions;

/**
 * Localizable is the superclass to all the Exceptions thrown by Localizable at Runtime.
 */
public class LocalizableException extends RuntimeException {

  /**
   * Default constructor.
   *
   * @param message Message to be shown
   */
  protected LocalizableException(final String message) {
    super(message);
  }
}
