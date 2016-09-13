package io.localizable.sdk.exceptions;

/**
 * NoStringsClassFoundException is thrown when the SDK cannot find the project R.strings class.
 */
public class NoStringsClassFoundException extends LocalizableException {

  /**
   * Default constructor.
   */
  public NoStringsClassFoundException() {
    super("Localizable could not find the R.String class.");
  }

}
