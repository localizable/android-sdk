package io.localizable.sdk.utils;

import io.localizable.sdk.networking.async.JsonCallback;
import okhttp3.Call;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Wrapper to JsonCallback that returns the parsed response or error.
 */
public abstract class SupportedLanguagesCallback extends JsonCallback {

  protected abstract void onDefaultLanguageChanged(List<String> languages);

  @Override
  protected void onJsonResponse(Call call, JSONObject json) {
    List<String> languages = fromJson(json);
    if (languages == null) {
      onFailure(call, null);
      return;
    }
    onDefaultLanguageChanged(languages);
  }

  private LinkedList<String> fromJson(JSONObject json) {
    try {
      JSONArray languageCodes = json.getJSONArray("languages");
      LinkedList<String> strings = new LinkedList<>();
      for (int i = 0; i < languageCodes.length(); i++) {
        strings.add(languageCodes.getString(i));
      }
      return strings;
    } catch (Exception exception) {
      LocalizableLog.warning(exception);
      return null;
    }
  }
}
