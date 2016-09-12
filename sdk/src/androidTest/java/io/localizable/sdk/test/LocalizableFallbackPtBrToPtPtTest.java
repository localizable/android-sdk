package io.localizable.sdk.test;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import io.localizable.sdk.Localizable;
import io.localizable.sdk.LocalizableBaseAndroidTest;
import io.localizable.sdk.networking.LocalizableOperation;
import io.localizable.sdk.networking.client.NetworkMocker;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class LocalizableFallbackPtBrToPtPtTest extends LocalizableBaseAndroidTest {


  @Override
  public void setup() {
    super.setup();
    NetworkMocker.addResponseForOperation("supported_languages_default_pt_pt",
        LocalizableOperation.languageCodes("token"), testApplicationContext());

    NetworkMocker.addResponseForOperation("pt_pt",
        LocalizableOperation.updateLanguage("pt-PT", 0, "token"), testApplicationContext());
  }

  @Test
  public void checkLocalizableStringsGetString() throws InterruptedException {
    Localizable.setLocale(new Locale("pt", "BR"));
    assertThat(Localizable.getString(R.string.string1), equalTo("pt-PT"));
  }
}
