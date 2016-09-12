package io.localizable.sdk.test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import android.support.test.runner.AndroidJUnit4;

import io.localizable.sdk.Localizable;
import io.localizable.sdk.LocalizableBaseAndroidTest;
import io.localizable.sdk.networking.client.NetworkMocker;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LocalizableColdStartWithNetworkSuccessTest extends LocalizableBaseAndroidTest {

  @Override
  public void setup() {
    super.setup();
    NetworkMocker.setupMockedResponsesBaseStrings(testApplicationContext());
  }

  @Test
  public void checkLocalizableStringsGetString() throws InterruptedException {
    Localizable.setup(testApplicationContext());
    assertThat(Localizable.getString(R.string.string1), equalTo("en"));
  }
}
