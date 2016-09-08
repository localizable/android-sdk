package io.localizable.sdk.networking.callback;

import io.localizable.sdk.model.AppLanguage;
import io.localizable.sdk.networking.async.JsonCallback;
import io.localizable.sdk.utils.LocalizableLog;

import okhttp3.Call;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;


public abstract class LocalizableAppLanguageCallback extends JsonCallback {

  protected abstract void onResponse(Call call, AppLanguage response);

  /**
   * Parses the JSONObject to AppLanguage and if it fails return null.
   *
   * @param json JSON file to parse to init class
   * @return AppLanguage file or null if JSON is invalid
   */
  private AppLanguage fromJson(JSONObject json) {
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

      LocalizableLog.debug("Received updates: "
          + new AppLanguage(code, languages, modifiedAt).toString());

      return new AppLanguage(code, languages, modifiedAt);
    } catch (JSONException exception) {
      LocalizableLog.error(exception);
    }
    return null;
  }

  @Override
  public void onJsonResponse(Call call, JSONObject json) {
    AppLanguage parsedResponse = fromJson(json);
    if (parsedResponse != null) {
      onResponse(call, parsedResponse);
    } else {
      onFailure(call, null);
    }
  }
}