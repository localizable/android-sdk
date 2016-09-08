package io.localizable.sdk.networking.async;

import io.localizable.sdk.utils.LocalizableLog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * JSON callback is a wrapper to return the response value as JSON or return failure.
 */

public abstract class JsonCallback implements Callback {
  @Override
  public void onResponse(Call call, Response response) throws IOException {
    try {
      onJsonResponse(call, new JSONObject(response.body().string()));
    } catch (JSONException exception) {
      LocalizableLog.warning(exception);
      onFailure(call, null);
    }
  }

  /**
   * Success response with a JSONObject.
   *
   * @param call The network call that was the origin of the request
   * @param json The response JSONObject
   */
  protected abstract void onJsonResponse(Call call, JSONObject json);

}
