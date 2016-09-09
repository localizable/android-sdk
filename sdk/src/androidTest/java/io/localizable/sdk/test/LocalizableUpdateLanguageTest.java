package io.localizable.sdk.test;


import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import io.localizable.sdk.Localizable;
import io.localizable.sdk.LocalizableTestUtils;
import io.localizable.sdk.networking.client.NetworkMocker;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class LocalizableUpdateLanguageTest {

  Context context() {
    return getInstrumentation().getContext();
  }

  @Before
  public void setup() {
    LocalizableTestUtils.deleteAllFiles(context());
    LocalizableTestUtils.setBaseLanguageEnglish(context());
    NetworkMocker.setupMockedResponsesBaseStrings(context());
    Localizable.setup(context());
    LocalizableTestUtils.waitForSetup();
  }

  @Test
  public void checkLocalizableStringsGetString() throws InterruptedException {
    Localizable.setLocale(new Locale("pt", "BR"));
    assertThat(Localizable.getString(R.string.string1), equalTo("pt-br"));
  }
}
