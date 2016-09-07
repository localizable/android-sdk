package io.localizable.demo.sdk.utils;

import java.io.Serializable;

/**
 * Supported languages callback.
 */
public abstract class SupportedLanguagesChangesCallback implements Serializable {

  public void errorSyncing() { }

  /**
   * No changes from Localized locales.
   */
  public abstract void onNoChangesDetected();

  /**
   * New Localized location if changed.
   */
  public abstract void onDefaultLanguageChanged(String newDefaultLanguageCode);
}
