package io.localizable.demo.sdk.networking.async;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.localizable.demo.sdk.utils.LocalizableLog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Network {
  private static OkHttpClient client;

  // Constants
  private static final int TASK_TIMEOUT = 15; // seconds

  public static OkHttpClient getClient() {
    return client;
  }

  public static void Setup() {
    Network.Setup(NetworkType.PRODUCTION);
  }

  public static void Setup(NetworkType buildType) {
    switch (buildType) {
      case TEST:
        Network.client = new LocalizableMockClient();
        LocalizableMockResponses.loadResponses();
        break;
      case PRODUCTION:
      Network.client = new OkHttpClient.Builder()
          .connectTimeout(TASK_TIMEOUT, TimeUnit.SECONDS)
          .build();
        break;
    }
  }

  public enum NetworkType {
    TEST, PRODUCTION
  }
}

class LocalizableMockClient extends OkHttpClient {
  @Override
  public Call newCall(Request request) {
    return new LocalizableMockCall(request);
  }
}

class LocalizableMockCall implements Call {

  private Request originalRequest;
  private boolean executed;

  public LocalizableMockCall(Request request) {
    this.originalRequest = request;
    this.executed = false;
  }

  @Override
  public Request request() {
    return null;
  }

  @Override
  public Response execute() throws IOException {
    return LocalizableMockResponses.responseToRequest(originalRequest);
  }

  @Override
  public void enqueue(final Callback responseCallback) {
    new AsyncTask<Void,Void, Response>() {
      @Override
      protected Response doInBackground(Void... voids) {
        try {
          return LocalizableMockCall.this.execute();
        } catch (IOException e) {
          return null;
        }
      }

      @Override
      protected void onPostExecute(Response response) {
        if (response != null) {
          try {
            responseCallback.onResponse(LocalizableMockCall.this, response);
          } catch (IOException e) {
            responseCallback.onFailure(LocalizableMockCall.this, null);
          }
        } else {
          responseCallback.onFailure(LocalizableMockCall.this, null);
        }
      }
    }.execute();
  }

  @Override
  public void cancel() {}

  @Override
  public boolean isExecuted() {
    return executed;
  }

  @Override
  public boolean isCanceled() {
    return false;
  }
}

class LocalizableMockResponses {

  private static HashMap<String, String> responses;

  public static void loadResponses() {
    HashMap<String, String> responses = new HashMap<>();
    responses.put("/api/v1/languages/en", "{\"modified_at\":1000,\"code\":\"en\",\"keywords\":{\"app_name\":\"LocalizableEN\"}}");
    responses.put("/api/v1/languages/base", "{\"modified_at\":1000,\"code\":\"en\",\"keywords\":{\"app_name\":\"LocalizableBASE\"}}");
    LocalizableMockResponses.responses = responses;
  }

  private static String responseForRequest(Request request, boolean startsWith) {
    String response = null;
    if (startsWith) {
      response = responses.get(request.url().encodedPath());
    } else {
      response = responses.get(request.url().encodedPath());
    }

    if (response == null)
      response = "{}";
    return response;
  }

  public static Response responseToRequest(Request request) {
    return new Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_0)
        .code(200)
        .body(ResponseBody.create(MediaType.parse("application/json"), responseForRequest(request, true)))
        .build();
  }
}