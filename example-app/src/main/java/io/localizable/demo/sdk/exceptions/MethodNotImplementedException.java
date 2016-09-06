package io.localizable.demo.sdk.exceptions;

/**
 * NoSDKTokenFoundException is thrown when the SDK token is not found on the manifest file.
 */
public class MethodNotImplementedException extends LocalizableException {

    /**
     * Default constructor.
     */
    public MethodNotImplementedException() {
        super("Method not implemented exception");
    }

}
