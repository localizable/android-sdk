package io.localizable.demo.sdk.model;

import android.content.Context;

import io.localizable.demo.sdk.utils.FileLoader;
import io.localizable.demo.sdk.utils.LocalizableLog;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Direct representation of a Localizable language.
 */
public class AppLanguage implements Serializable {

  /**
   * Last updated time.
   */
  private long modifiedAt;

  /**
   * Language code, the default language code is "base" represented in the "values" folder.
   */
  private String code;

  /**
   * Localized strings represented by key - string "name" in resources and it's translation.
   */
  private HashMap<String, String> strings;

  /**
   * Current language filename.
   */
  static final String LOCALIZABLE_LANGUAGE_FILE_NAME = "language.dex";

  /**
   * Complete constructor.
   *
   * @param languageCode Localizable language code
   * @param languageStrings Language strings
   * @param lastModifiedAt Last update time
   */
  public AppLanguage(String languageCode, HashMap<String, String> languageStrings,
                     Long lastModifiedAt) {
    code = languageCode;
    strings = languageStrings;
    modifiedAt = lastModifiedAt;
  }

  /**
   * Creates an AppLanguage with lastUpdate time 0.
   * @param languageCode Localizable language code
   * @param languageStrings Language strings
   */
  public AppLanguage(String languageCode, HashMap<String, String> languageStrings) {
    this(languageCode, languageStrings, 0L);
  }

  public String getCode() {
    return code;
  }

  public HashMap<String, String> getStrings() {
    return strings;
  }

  public void setModifiedAt(long modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

  public long getModifiedAt() {
    return modifiedAt;
  }

  /**
   * Store instance to disk.
   *
   * @param context Application context
   */
  public final void saveToDisk(Context context) {
    LocalizableLog.debug("Saving language to File: " + toString());
    new FileLoader<AppLanguage>(context, LOCALIZABLE_LANGUAGE_FILE_NAME).store(this);
  }

  /**
   * Update Language with new tokens from language.
   *
   * @param updates New language updates
   */
  public final void update(AppLanguage updates) {
    if (updates == null) {
      LocalizableLog.error("Trying to update Language with invalid object");
      return;
    }
    LocalizableLog.debug("Updating current Language with language: " + updates.toString());
    for (String key: updates.strings.keySet()) {
      strings.put(key, updates.strings.get(key));
    }
    modifiedAt = new Date().getTime();
  }

  @Override
  public final String toString() {
    return "AppLanguage{"
        + "modifiedAt=" + modifiedAt
        + ", code='" + code + '\''
        + ", strings=" + strings.toString()
        + '}';
  }

  /**
   * Load an instance of AppLanguage from disk.
   *
   * @param context Application context.
   * @return Localizable AppLanguage stored in the local file or null if the file is not found
   */
  public static AppLanguage loadAppLanguageFromDisk(Context context) {
    return new FileLoader<AppLanguage>(context, LOCALIZABLE_LANGUAGE_FILE_NAME).loadFile();
  }
}

