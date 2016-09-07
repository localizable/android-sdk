package io.localizable.demo.sdk.networking.client;

import android.os.AsyncTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Call Used by localizable Mock client to return sample JSONs instead of the Network requests.
 */
class LocalizableMockCall implements Call {

  /**
   * Request that originated the Call.
   */
  private Request originalRequest;

  /**
   * State of the call.
   */
  private boolean executed;

  /**
   * Call has been canceled.
   */
  private boolean canceled;

  /**
   * Default constructor for mock call need the request.
   *
   * @param request Request to execute
   */
  LocalizableMockCall(final Request request) {
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
   * Request was already canceled.
   *
   * @return Executed request state
   */
  public boolean wasCanceled() {
    return canceled;
  }

  /**
   * Sets the request as canceled.
   */
  void cancelRequest() {
    canceled = true;
  }

  /**
   * Request was already executed.
   *
   * @return Executed request state
   */
  boolean wasExecuted() {
    return executed;
  }

  /**
   * Sets the request as executed.
   */
  void finishedExecution() {
    executed = true;
  }

  /**
   * Enqueue the Request - Simulate an async call and call the Callback.
   *
   * @param responseCallback Success/Failure callback
   */
  @Override
  public void enqueue(final Callback responseCallback) {
    new AsyncTask<Void, Void, Response>() {
      @Override
      protected Response doInBackground(final Void... voids) {
        try {
          return LocalizableMockCall.this.execute();
        } catch (IOException ignored) {
          return null;
        }
      }

      @Override
      protected void onPostExecute(final Response response) {
        if (wasCanceled()) {
          responseCallback.onFailure(LocalizableMockCall.this, null);
          return;
        }

        if (response != null) {
          try {
            responseCallback.onResponse(LocalizableMockCall.this, response);
          } catch (IOException ignored) {
            responseCallback.onFailure(LocalizableMockCall.this, null);
          }
        } else {
          responseCallback.onFailure(LocalizableMockCall.this, null);
        }
        LocalizableMockCall.this.finishedExecution();
      }
    }.execute();
  }

  @Override
  public void cancel() {
    cancelRequest();
  }

  @Override
  public boolean isExecuted() {
    return wasExecuted();
  }

  @Override
  public boolean isCanceled() {
    return wasCanceled();
  }
}
