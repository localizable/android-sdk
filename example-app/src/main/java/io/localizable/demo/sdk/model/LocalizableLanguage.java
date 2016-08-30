package io.localizable.demo.sdk.model;

import android.content.Context;
import android.util.SparseArray;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import io.localizable.demo.sdk.networking.LocalizableOperation;
import io.localizable.demo.sdk.networking.async.HttpRequest;
import io.localizable.demo.sdk.networking.async.JSONCallback;
import io.localizable.demo.sdk.utils.LocalizableLog;
import okhttp3.Call;

public class LocalizableLanguage {
  String apiToken;
  String code;
  SparseArray<String> strings;
  SparseArray<String> appStrings;

  public LocalizableLanguage(SparseArray<String> appStrings, Context context, String apiToken) {
    this.appStrings = appStrings;
    this.apiToken = apiToken;
    this.initData(context);
  }

  public String getString(Context context, int resID) {
    if (strings.get(resID) != null)
      return strings.get(resID);
    return context.getResources().getString(resID);
  }

  @Override
  public String toString() {
    return "LocalizableLanguage {" +
        "apiToken='" + apiToken + '\'' +
        ", code='" + code + '\'' +
        ", strings=" + strings.toString() +
        ", appStrings=" + appStrings.toString() +
        '}';
  }

  private static SparseArray<String> sparseArrayFromHashMap(HashMap<String, String> strings,
                                                            SparseArray<String> appStrings) {
    try {
      SparseArray<String> localizableStrings = new SparseArray<>();
      for(int index = 0; index <= appStrings.size(); index ++) {
        int key = appStrings.keyAt(index);
        String value = appStrings.valueAt(index);
        String string = strings.get(value);
        localizableStrings.put(key ,string);
      }
      return localizableStrings;
    } catch(Exception e) {
      return null;
    }
  }

  private void initData(Context context) {
    LocalizableAppLanguage appLanguage = loadCurrentAppLanguage(context);
    updateValues(appLanguage);
    updateStrings(appLanguage, context);
  }

  void updateValues(LocalizableAppLanguage appLanguage) {
    this.code = appLanguage.code;
    this.strings = sparseArrayFromHashMap(appLanguage.strings, appStrings);
  }

  private LocalizableAppLanguage loadCurrentAppLanguage(Context context) {
    LocalizableAppLanguage appLanguage = LocalizableAppLanguage.loadFromDisk(context);
    if (appLanguage == null) {
      appLanguage = LocalizableAppLanguage.deviceLanguage(context);
      LocalizableLog.debug("Setting default language: " + appLanguage.toString());
    } else {
      LocalizableLog.debug("Loaded language from File: " + appLanguage.toString());
    }
    return appLanguage;
  }

  void updateStrings(final LocalizableAppLanguage appLanguage, final Context context) {
    HttpRequest request = new HttpRequest(LocalizableOperation.UpdateLanguage(appLanguage.code, appLanguage.modifiedAt, apiToken));
    request.execute(new JSONCallback() {
      @Override
      public void onFailure(Call call, IOException e) {
        LocalizableAppLanguage defaultLanguage = LocalizableAppLanguage.defaultLanguage();
        if (appLanguage.code.equals(defaultLanguage.code)) {
          LocalizableLog.error(e);
          return;
        }
        updateStrings(defaultLanguage, context);
      }

      @Override
      public void onJSONResponse(Call call, JSONObject json) {
        if (json == null) {
          LocalizableLog.error("Invalid JSON Object");
          return;
        }
        appLanguage.update(LocalizableAppLanguage.fromJSON(json));
        updateValues(appLanguage);
        appLanguage.saveToDisk(context);
        LocalizableLog.debug("Updated language -> " + appLanguage.toString());
      }
    });
  }
}

class FileLoader<T extends Serializable> {
  private Context context;
  private String name;

  //Localizable folder name
  private static final String FOLDER_NAME = "localizable";

  /**
   * Helper to store and load a serializable class from disk
   *
   * @param context Application context to get the app specific file DIR
   * @param name Name of the file to store/load
   */
  public FileLoader(Context context, String name) {
    this.context = context;
    this.name = name;
  }

  /**
   * Load the file with the name
   *
   * @return An instance of the T class or null if class is not found/invalid file
   */
  public T loadFile() {
    try {
      File file = fileFromFilename(name, context);
      if (!file.exists() || !file.isFile()) {

        return null;
      }
      FileInputStream fileInputStream = new FileInputStream(file);
      ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
      T returnValue = (T) objectInputStream.readObject();
      fileInputStream.close();
      objectInputStream.close();
      return returnValue;
    } catch (ClassNotFoundException | IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Store the file in the Localizable Folder
   *
   * @param instance The object to store
   */
  public void store(T instance) {
    try {
      File file = fileFromFilename(name, context);
      FileOutputStream fileStream = new FileOutputStream(file);
      ObjectOutputStream os = new ObjectOutputStream(fileStream);
      os.writeObject(instance);
      os.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Helper function to return the File from a given filename.
   *
   * @param filename The filename corresponding to the File.
   * @param context  A context object.
   * @return The File.
   */
  private static File fileFromFilename(String filename, Context context) {
    File directory = context.getDir(FOLDER_NAME, Context.MODE_PRIVATE);
    return new File(directory, filename);
  }
}