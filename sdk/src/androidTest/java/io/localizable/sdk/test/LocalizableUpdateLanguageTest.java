package io.localizable.sdk.test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import android.support.test.runner.AndroidJUnit4;

import io.localizable.sdk.Localizable;
import io.localizable.sdk.LocalizableBaseAndroidTest;
import io.localizable.sdk.networking.client.NetworkMocker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class LocalizableUpdateLanguageTest extends LocalizableBaseAndroidTest {

  @Before
  public void setup() {
    super.setup();
    NetworkMocker.setupMockedResponsesBaseStrings(testApplicationContext());
  }

  @Test
  public void checkLocalizableStringsGetString() throws InterruptedException {
    Localizable.setup(testApplicationContext());
    Localizable.setLocale(new Locale("pt", "BR"));
    assertThat(Localizable.getString(R.string.string1), equalTo("pt-br"));
  }
}
