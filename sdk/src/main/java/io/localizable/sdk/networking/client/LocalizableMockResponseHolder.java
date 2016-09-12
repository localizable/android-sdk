package io.localizable.sdk.networking.client;

import android.content.Context;

import io.localizable.sdk.networking.HttpOperation;
import io.localizable.sdk.utils.LocalizableLog;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

class LocalizableMockResponseHolder {

  private static HashMap<String, String> responses;

  private static void setup() {
    LocalizableMockResponseHolder.responses = new HashMap<>();
  }

  public static void clear() {
    setup();
  }

  public static void addResponseForOperation(String fileName, HttpOperation operation,
                                             Context context) {
    if (responses == null) {
      setup();
    }

    String response = readFileJsonFromAssets(fileName, context);
    if (response == null) {
      response = "{}";
    }
    LocalizableMockResponseHolder.responses.put(operation.getEncodedPath(), response);
  }

  private static String responseForRequest(Request request, boolean startsWith) {
    if (responses == null) {
      setup();
    }
    String response = null;
    String path = request.url().encodedPath();
    if (startsWith) {
      for (String key : responses.keySet()) {
        if (path.startsWith(key)) {
          response = responses.get(request.url().encodedPath());
        }
      }
    } else {
      response = responses.get(request.url().encodedPath());
    }

    if (response == null) {
      response = "{}";
    }
    return response;
  }

  public static Response responseToRequest(Request request) {
    return new Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_0)
        .code(200)
        .body(ResponseBody.create(MediaType.parse("application/json"),
            responseForRequest(request, false)))
        .build();
  }


  private static String readFileJsonFromAssets(String fileName, Context context) {
    try {

      InputStream is = context.getAssets().open(fileName + ".json");
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      return new String(buffer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
