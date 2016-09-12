package io.localizable.sdk.test;


import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import io.localizable.sdk.Localizable;
import io.localizable.sdk.LocalizableBaseAndroidTest;
import io.localizable.sdk.networking.client.NetworkMocker;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class LocalizableUpdateLanguageWithNetworkTest extends LocalizableBaseAndroidTest {

  @Before
  public void setup() {
    super.setup();
    NetworkMocker.setupMockedResponsesBaseStrings(testApplicationContext());
  }

  @Test
  public void checkLocalizableStringsGetString() throws InterruptedException {
    Localizable.setup(testApplicationContext());
    assertThat(Localizable.getString(R.string.string1), equalTo("en"));

    NetworkMocker.setupMockedResponseForPtBr(testApplicationContext());
    Localizable.setLocale(new Locale("pt", "BR"));
    assertThat(Localizable.getString(R.string.string1), equalTo("pt-BR"));
  }
}
