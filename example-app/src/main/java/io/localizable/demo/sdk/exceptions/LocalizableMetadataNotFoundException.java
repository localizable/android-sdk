package io.localizable.demo.sdk.exceptions;

/**
 * NoSdkTokenFoundException is thrown when the SDK token is not found on the manifest file.
 * Check ManifestUtils for more information.
 */
public class LocalizableMetadataNotFoundException extends LocalizableException {

  /**
   * Default constructor.
   */
  public LocalizableMetadataNotFoundException() {
    super("Could not find the Localizable SDK key in manifest");
  }
}
