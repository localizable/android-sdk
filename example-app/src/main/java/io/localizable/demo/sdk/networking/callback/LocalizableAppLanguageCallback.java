package io.localizable.demo.sdk.networking.callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import io.localizable.demo.sdk.model.AppLanguage;
import io.localizable.demo.sdk.networking.async.JSONCallback;
import io.localizable.demo.sdk.utils.LocalizableLog;
import okhttp3.Call;

public abstract class LocalizableAppLanguageCallback extends JSONCallback {

  protected abstract void onResponse(Call call, AppLanguage response);

  /**
   *
   * @param json JSON file to parse to init class
   * @return AppLanguage file or null if JSON is invalid
   */
  private AppLanguage fromJSON(JSONObject json) {
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
          new AppLanguage(code, languages, modifiedAt).toString());

      return new AppLanguage(code, languages, modifiedAt);
    } catch (JSONException e) {
      LocalizableLog.error(e);
    }
    return null;
  }

  @Override
  public void onJSONResponse(Call call, JSONObject json) {
    AppLanguage parsedResponse = fromJSON(json);
    if (parsedResponse != null) {
      onResponse(call, parsedResponse);
    } else {
      onFailure(call, null);
    }
  }
}