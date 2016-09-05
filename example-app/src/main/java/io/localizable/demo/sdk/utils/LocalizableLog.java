package io.localizable.demo.sdk.utils;

public class LocalizableLog {

  private static String LocalizablePrefix = "Localizable ~~> ";
  public static LocalizableLogLevel logLevel = LocalizableLogLevel.DEBUG;

  public static void error(String error) {
    System.out.println(LocalizablePrefix + "Error " + error);
  }

  public static void error(Exception error) {
    System.out.println(LocalizablePrefix + "Error " + error.getMessage());
  }

  public static void warning(String error) {
    if (logLevel.value > LocalizableLogLevel.WARNING.value) {
      return;
    }
    System.out.println(LocalizablePrefix + "Warning " + error);
  }

  public static void warning(Exception error) {
    if (logLevel.value > LocalizableLogLevel.WARNING.value) {
      return;
    }
    System.out.println(LocalizablePrefix + "Warning " + error.getMessage());
  }

  public static void debug(String error) {
    if (logLevel.value > LocalizableLogLevel.DEBUG.value) {
      return;
    }
    System.out.println(LocalizablePrefix + "Debug " + error);
  }

  public static void debug(Exception error) {
    if (logLevel.value > LocalizableLogLevel.DEBUG.value) {
      return;
    }
    System.out.println(LocalizablePrefix + "Debug " + error.getMessage());
  }

  public enum LocalizableLogLevel {
    DEBUG(0), WARNING(1), ERROR(2);

    LocalizableLogLevel(int value) {
      this.value = value;
    }
    public int value;
  }
}
