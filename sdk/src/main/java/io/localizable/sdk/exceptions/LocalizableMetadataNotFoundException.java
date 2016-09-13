package io.localizable.sdk.exceptions;

/**
 * LocalizableMetadataNotFoundException is thrown when the SDK cannot fetch the metadata token
 * from the manifest file.
 * <br>
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
