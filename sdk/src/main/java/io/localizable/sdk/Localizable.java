package io.localizable.sdk;

import android.content.Context;

import javax.annotation.Nonnull;

import io.localizable.sdk.exceptions.NoSDKTokenFoundException;
import io.localizable.sdk.utils.ManifestUtils;

public class Localizable {

  public static void setup(@Nonnull Context context) {
    try {
      System.out.println("-> " + ManifestUtils.getAPITokenFromMetadata(context));

      System.out.println("" + context.getPackageName() + ".R.string");

    } catch (NoSDKTokenFoundException e) {
      e.printStackTrace();
    }
  }
}
