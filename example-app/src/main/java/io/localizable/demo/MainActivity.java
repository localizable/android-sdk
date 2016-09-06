package io.localizable.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Locale;

import io.localizable.demo.sdk.Localizable;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Log.e("LOCALIZABLE", Localizable.getString(R.string.greeting_message, "Bruno"));
    Localizable.setLocale(new Locale("pt", "PT"));
    Log.e("LOCALIZABLE", Localizable.getString(R.string.greeting_message, "Bruno"));
  }
}
