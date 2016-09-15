package io.localizable.example;

import android.app.Application;

import io.localizable.sdk.Localizable;

public class MyApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Localizable.setup(this);

  }

}