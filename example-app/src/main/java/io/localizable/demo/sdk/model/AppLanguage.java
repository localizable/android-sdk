package io.localizable.demo.sdk.model;

import android.content.Context;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import io.localizable.demo.sdk.utils.FileLoader;
import io.localizable.demo.sdk.utils.LocalizableLog;

public class AppLanguage implements Serializable {

  long modifiedAt;
  String code;
  HashMap<String, String> strings;
  static String LocalizableLanguageFileName = "language.dex";

  public AppLanguage(String code, HashMap<String, String> strings, Long modifiedAt) {
    this.code = code;
    this.strings = strings;
    this.modifiedAt = modifiedAt;
  }

  public AppLanguage(String code, HashMap<String, String> strings) {
    this(code, strings, 0L);
  }

  public void saveToDisk(Context context) {
    LocalizableLog.debug("Saving language to File: " + toString());
    new FileLoader<AppLanguage>(context, LocalizableLanguageFileName).store(this);
  }

  /**
   * Update Language with new tokens from language
   *
   * @param updates New language updates
   */
  public void update(AppLanguage updates) {
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
  public String toString() {
    return "AppLanguage{" +
        "modifiedAt=" + modifiedAt +
        ", code='" + code + '\'' +
        ", strings=" + strings.toString() +
        '}';
  }

  /**
   *
   * @param context Application context
   * @return Localizable AppLanguage stored in the local file or null if the file is not found
   */
  public static AppLanguage loadAppLanguageFromDisk(Context context) {
    return new FileLoader<AppLanguage>(context, LocalizableLanguageFileName).loadFile();
  }
}

