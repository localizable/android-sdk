package io.localizable.demo.sdk.networking.async;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * JSON callback is a wrapper to return the response value as JSON or return failure,
 *
 */

public abstract class JSONCallback implements Callback {
  @Override
  public void onResponse(Call call, Response response) throws IOException {
    try {
      onJSONResponse(call, new JSONObject(response.body().string()));
    } catch (JSONException e) {
      onFailure(call, null);
    }
  }

  /**
   * Success response with a JSONObject
   *
   * @param call The network call that was the origin of the request
   * @param json The response JSONObject
   */
  public abstract void onJSONResponse(Call call, JSONObject json);
}
