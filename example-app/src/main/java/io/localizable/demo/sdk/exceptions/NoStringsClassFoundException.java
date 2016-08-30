package io.localizable.demo.sdk.exceptions;

/**
 * NoSDKTokenFoundException is thrown when the SDK token is not found on the manifest file.
 */
public class NoStringsClassFoundException extends LocalizableException {
    public NoStringsClassFoundException() {
        super("Localizable could not find the R.String class.");
    }

}
