package io.localizable.demo.sdk.networking.client;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Call Used by localizable Mock client to return sample JSONs instead of the Network requests
 */
class LocalizableMockCall implements Call {

  /**
   * Request that originated the Call
   */
  private Request originalRequest;

  /**
   * State of the call
   */
  boolean executed;

  /**
   * Call has been canceled
   */
  boolean canceled;

  /**
   * Default constructor for mock call need the request
   *
   * @param request Request to execute
   */
  public LocalizableMockCall(Request request) {
    this.originalRequest = request;
    this.executed = false;
    this.canceled = false;
  }

  @Override
  public Request request() {
    return originalRequest;
  }

  @Override
  public Response execute() throws IOException {
    return LocalizableMockResponseHolder.responseToRequest(originalRequest);
  }

  /**
   * Enqueue the Request - Simulate an async call and call the Callback
   *
   * @param responseCallback Success/Failure callback
   */
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
        if (canceled) {
          responseCallback.onFailure(LocalizableMockCall.this, null);
          return;
        }

        if (response != null) {
          try {
            responseCallback.onResponse(LocalizableMockCall.this, response);
          } catch (IOException e) {
            responseCallback.onFailure(LocalizableMockCall.this, null);
          }
        } else {
          responseCallback.onFailure(LocalizableMockCall.this, null);
        }
        LocalizableMockCall.this.executed = true;
      }
    }.execute();
  }

  @Override
  public void cancel() {
    canceled = true;
  }

  @Override
  public boolean isExecuted() {
    return executed;
  }

  @Override
  public boolean isCanceled() {
    return canceled;
  }
}
