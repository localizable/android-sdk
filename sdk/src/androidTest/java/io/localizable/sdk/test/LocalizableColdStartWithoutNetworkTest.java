package io.localizable.sdk.test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import android.support.test.runner.AndroidJUnit4;

import io.localizable.sdk.Localizable;
import io.localizable.sdk.LocalizableBaseAndroidTest;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LocalizableColdStartWithoutNetworkTest extends LocalizableBaseAndroidTest {

  @Test
  public void initialize() {
    Localizable.setup(testApplicationContext());
    assertThat(Localizable.getString(R.string.string1),
        equalTo(getInstrumentation().getContext().getString(R.string.string1)));
  }
}
