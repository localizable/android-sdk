package io.localizable.sdk.utils;

@SuppressWarnings("unused")
public class LocalizableLog {

  private static String LocalizablePrefix = "Localizable ~~> ";

  public static LocalizableLogLevel logLevel = LocalizableLogLevel.ERROR;


  /**
   * Log sdk Error message.
   *
   * @param message message to log
   */
  public static void error(String message) {
    System.out.println(LocalizablePrefix + "Error " + message);
  }


  /**
   * Log sdk Error exception.
   *
   * @param exception exception to log
   */
  public static void error(Exception exception) {
    if (exception == null) {
      return;
    }
    System.out.println(LocalizablePrefix + "Error " + exception.getMessage());
  }


  /**
   * Log sdk Warning message.
   *
   * @param message message to log
   */
  public static void warning(String message) {
    if (logLevel.value > LocalizableLogLevel.WARNING.value) {
      return;
    }
    System.out.println(LocalizablePrefix + "Warning " + message);
  }


  /**
   * Log sdk warning exception.
   *
   * @param exception exception to log
   */
  public static void warning(Exception exception) {
    if (logLevel.value > LocalizableLogLevel.WARNING.value || exception == null) {
      return;
    }
    System.out.println(LocalizablePrefix + "Warning " + exception.getMessage());
  }


  /**
   * Log sdk Debug message.
   *
   * @param message message to log
   */
  public static void debug(String message) {
    if (logLevel.value > LocalizableLogLevel.DEBUG.value) {
      return;
    }
    System.out.println(LocalizablePrefix + "Debug " + message);
  }


  /**
   * Log sdk Debug exception.
   *
   * @param exception exception to log
   */
  public static void debug(Exception exception) {
    if (logLevel.value > LocalizableLogLevel.DEBUG.value || exception == null) {
      return;
    }
    System.out.println(LocalizablePrefix + "Debug " + exception.getMessage());
  }


  public enum LocalizableLogLevel {
    DEBUG(0), WARNING(1), ERROR(2);

    LocalizableLogLevel(int value) {
      this.value = value;
    }

    public int value;
  }
}
