package io.localizable.demo.sdk.model;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import io.localizable.demo.sdk.utils.LocalizableLog;

public class LocalizableAppLanguage implements Serializable {

  long modifiedAt;
  String code;
  HashMap<String, String> strings;
  static String LocalizableLanguageFileName = "language.dex";

  public LocalizableAppLanguage(String code, HashMap<String, String> strings, Long modifiedAt) {
    this.code = code;
    this.strings = strings;
    this.modifiedAt = modifiedAt;
  }

  public LocalizableAppLanguage(String code, HashMap<String, String> strings) {
    this(code, strings, 0L);
  }

  public void saveToDisk(Context context) {
    LocalizableLog.debug("Saving language to File: " + toString());
    new FileLoader<LocalizableAppLanguage>(context, LocalizableLanguageFileName).store(this);
  }

  /**
   * Update Language with new tokens from language
   *
   * @param updates New language updates
   */
  public void update(LocalizableAppLanguage updates) {
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
    return "LocalizableAppLanguage{" +
        "modifiedAt=" + modifiedAt +
        ", code='" + code + '\'' +
        ", strings=" + strings.toString() +
        '}';
  }

  /**
   *
   * @param context Application context
   * @return LocalizableAppLanguage with the active language on the device
   */
  public static LocalizableAppLanguage deviceLanguage(Context context) {
    return new LocalizableAppLanguage("en", new HashMap<String, String>());
  }

  /**
   *
   * @param context Application context
   * @return Localizable AppLanguage stored in the local file or null if the file is not found
   */
  public static LocalizableAppLanguage loadFromDisk(Context context) {
    return new FileLoader<LocalizableAppLanguage>(context, LocalizableLanguageFileName).loadFile();
  }

  /**
   *
   * @return Localizable AppLanguage with the "base" language code
   */
  public static LocalizableAppLanguage defaultLanguage() {
    return new LocalizableAppLanguage("base", new HashMap<String, String>());
  }

  /**
   *
   * @param json JSON file to parse to init class
   * @return LocalizableAppLanguage file or null if JSON is invalid
   */
  public static LocalizableAppLanguage fromJSON(JSONObject json) {
    if (json == null) {
      return null;
    }
    try {
      long modifiedAt = json.getLong("modified_at");
      String code = json.getString("code");
      JSONObject tokens = json.getJSONObject("keywords");
      Iterator tokenIterator = tokens.keys();
      HashMap<String, String> languages = new HashMap<>();
      while (tokenIterator.hasNext()) {
        String key = (String) tokenIterator.next();
        languages.put(key, tokens.getString(key));
      }

      LocalizableLog.debug("Recieved updates: " +
          new LocalizableAppLanguage(code, languages, modifiedAt).toString());

      return new LocalizableAppLanguage(code, languages, modifiedAt);
    } catch (JSONException e) {
      LocalizableLog.error(e);
    }
    return null;
  }
}
