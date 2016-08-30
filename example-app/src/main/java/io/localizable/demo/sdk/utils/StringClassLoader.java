package io.localizable.demo.sdk.utils;

import android.content.Context;
import android.util.SparseArray;

import java.lang.reflect.Field;

import io.localizable.demo.sdk.exceptions.NoStringsClassFoundException;

public class StringClassLoader {

  /**
   * Retrieves all the String Int values at Runtime.
   *
   * @param context application Context
   * @return A Sparse array where the keys contain the Int value to fetch the string resource.
   */
  public static SparseArray<String> loadStringsFromContext(Context context) throws NoStringsClassFoundException {
    return loadStringsFromClass(getStringsClass(context));
  }

  /**
   * Retrieves all the String Int values at Runtime.
   *
   * @param stringsClazz R.String classs
   * @return A Sparse array where the keys contain the Int value to fetch the string resource.
   */
  public static SparseArray<String> loadStringsFromClass(Class stringsClazz) {
    Field[] fields = stringsClazz.getDeclaredFields();
    SparseArray<String> stringTokens = new SparseArray<>();
    for (Field field : fields) {
      try {
        stringTokens.append(field.getInt(stringsClazz), field.getName());
      } catch (Exception e) { }
    }
    return stringTokens;
  }

  /**
   * Retrieves the BuildConfig class from a given context.
   * Will try to get it from the context's package name, otherwise it will go through the
   * package hierarchy trying to find a BuildConfig class.
   *
   * @param context A context object.
   * @return A Class object or null.
   */
  private static Class<?> getStringsClass(Context context) throws NoStringsClassFoundException {
    Class<?> klass = getBuildRStringClassFromPackage(context.getPackageName(), true);
    if (klass == null) {
      klass = getBuildRStringClassFromPackage(context.getClass().getPackage().getName(), true);
    }

    if (klass == null) {
      throw new NoStringsClassFoundException();
    }

    return klass;
  }

  /**
   * Retrieves the BuildConfig class from a package name.
   * Will try to get it from the supplied package name, if the class is not found, depending on
   * the traverse value it will try to recursively find the class on the package hierarchy.
   *
   * @param packageName The package name.
   * @param traverse    true if it should traver the package hierarchy, false otherwise.
   * @return A Class object or null.
   */
  private static Class<?> getBuildRStringClassFromPackage(String packageName, boolean traverse) {
    try {
      return Class.forName(packageName + ".R$string");
    } catch (ClassNotFoundException e) {
      if (traverse) {
        int indexOfLastDot = packageName.lastIndexOf('.');
        if (indexOfLastDot != -1) {
          return getBuildRStringClassFromPackage(packageName.substring(0, indexOfLastDot),
              true);
        }
      }
      return null;
    }
  }
}
