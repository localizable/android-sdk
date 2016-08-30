package io.localizable.sdk.exceptions;

import io.localizable.sdk.utils.ManifestUtils;


/**
 * NoSDKTokenFoundException is thrown when the SDK token is not found on the manifest file.
 */
public class NoSDKTokenFoundException extends LocalizableException {
    public NoSDKTokenFoundException() {
        super("Localizable could not find a " + ManifestUtils.SDK_TOKEN_METADATA_KEY +
                " meta-tag in your AndroidManifest.xml file.");
    }

}
