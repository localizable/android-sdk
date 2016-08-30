package io.localizable.demo;

import android.app.Application;

import io.localizable.demo.sdk.Localizable;

public class MyApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Localizable.setup(this);

  }

}