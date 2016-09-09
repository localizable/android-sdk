package io.localizable.sdk.test;


import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.localizable.sdk.*;


@RunWith(AndroidJUnit4.class)
public class LocalizableInitializationTest {

  Context context() {
    return getInstrumentation().getContext();
  }

  @Before
  public void setup() {
    LocalizableTestUtils.deleteAllFiles(context());
    LocalizableTestUtils.setBaseLanguageEnglish(context());
    Localizable.setup(getInstrumentation().getContext());
  }

  @Test
  public void initialize() {
    assertThat(Localizable.getString(R.string.string1),
        equalTo(getInstrumentation().getContext().getString(R.string.string1)));
  }
}
