package io.localizable.demo.sdk.utils;

public class LocalizableLog {

  private static String LocalizablePrefix = "Localizable ~~> ";

  public static void error(String error) {
    System.out.println(LocalizablePrefix + "Error " + error);
  }

  public static void warning(String error) {
    System.out.println(LocalizablePrefix + "Warning " + error);
  }

  public static void debug(String error) {
    System.out.println(LocalizablePrefix + "Debug " + error);
  }

  public static void error(Exception error) {
    System.out.println(LocalizablePrefix + "Error " + error.getMessage());
  }

  public static void warning(Exception error) {
    System.out.println(LocalizablePrefix + "Warning " + error.getMessage());
  }

  public static void debug(Exception error) {
    System.out.println(LocalizablePrefix + "Debug " + error.getMessage());
  }
}
