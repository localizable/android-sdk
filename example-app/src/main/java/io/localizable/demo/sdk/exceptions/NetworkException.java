package io.localizable.demo.sdk.exceptions;

import io.localizable.demo.sdk.utils.ManifestUtils;

/**
 * NoSDKTokenFoundException is thrown when the SDK token is not found on the manifest file.
 */
public class NetworkException extends LocalizableException {
    public NetworkException(String message) {
        super("Error contacting server " + message);
    }

}
