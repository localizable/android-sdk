package io.localizable.demo.sdk.exceptions;

import io.localizable.demo.sdk.utils.ManifestUtils;

/**
 * NoSDKTokenFoundException is thrown when the SDK token is not found on the manifest file.
 */
public class NoSDKTokenFoundException extends LocalizableException {

  /**
   * Default constructor.
   */
  public NoSDKTokenFoundException() {
    super("Localizable could not find a " + ManifestUtils.SDK_TOKEN_METADATA_KEY
        + " meta-tag in your AndroidManifest.xml file.");
  }

}
