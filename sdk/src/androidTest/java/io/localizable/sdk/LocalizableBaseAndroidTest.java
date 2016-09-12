package io.localizable.sdk;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

import android.content.Context;

import io.localizable.sdk.networking.client.NetworkMocker;
import io.localizable.sdk.utils.LocalizableLog;
import io.localizable.sdk.utils.LocalizableSharedPreferences;

import org.junit.Before;

public abstract class LocalizableBaseAndroidTest {

  @Before
  public void setup() {
    LocalizableLog.logLevel = LocalizableLog.LocalizableLogLevel.ERROR;
    clearData();
  }

  protected Context testApplicationContext() {
    return getInstrumentation().getContext();
  }

  protected void clearData() {
    LocalizableTestUtils.deleteAllFiles(testApplicationContext());
    LocalizableSharedPreferences.clear(testApplicationContext());
    NetworkMocker.clearMockedResponses();
    LocalizableTestUtils.setBaseLanguageEnglish(testApplicationContext());
  }
}
