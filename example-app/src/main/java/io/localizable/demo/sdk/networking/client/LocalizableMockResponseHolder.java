package io.localizable.demo.sdk.networking.client;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.util.HashMap;

class LocalizableMockResponseHolder {

  private static HashMap<String, String> responses;

  public static void loadDefaultResponses() {
    HashMap<String, String> responses = new HashMap<>();
    responses.put("/api/v1/languages/platforms/android",
        "{\"languages\":[\"pt\",\"pt-BR\",\"base\",\"en\"]}");

    responses.put("/api/v1/languages/en",
        "{\"modified_at\":1000,\"code\":\"en\",\"keywords\":{\"app_name\":\"LocalizableEN\"}}");

    responses.put("/api/v1/languages/base",
        "{\"modified_at\":1000,\"code\":\"en\",\"keywords\":{\"app_name\":\"LocalizableBASE\"}}");

    LocalizableMockResponseHolder.responses = responses;
  }

  private static String responseForRequest(Request request, boolean startsWith) {
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
}
