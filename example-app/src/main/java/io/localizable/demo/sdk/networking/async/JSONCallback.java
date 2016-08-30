package io.localizable.demo.sdk.networking.async;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class JSONCallback implements Callback {
  @Override
  public void onResponse(Call call, Response response) throws IOException {
    try {
      onJSONResponse(call, new JSONObject(response.body().string()));
    } catch (JSONException e) {
      onFailure(call, null);
    }
  }

  public abstract void onJSONResponse(Call call, JSONObject json);
}
