package io.localizable.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import io.localizable.sdk.Localizable;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ((TextView) findViewById(R.id.hello_world_text_view))
        .setText(Localizable.getString(R.string.greeting_message, "Bruno"));
  }
}
