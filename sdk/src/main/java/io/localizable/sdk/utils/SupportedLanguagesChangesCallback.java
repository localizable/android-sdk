package io.localizable.sdk.utils;

import java.io.Serializable;

/**
 * Supported languages callback.
 */
public abstract class SupportedLanguagesChangesCallback implements Serializable {

  public void errorSyncing() { }

  /**
   * No changes from Localized locales.
   *
   * @param currentLanguage Current language code
   */
  public abstract void onNoChangesDetected(String currentLanguage);

  /**
   * New Localized location if changed.
   *
   * @param newDefaultLanguageCode New language code
   */
  public abstract void onDefaultLanguageChanged(String newDefaultLanguageCode);
}
