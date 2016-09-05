package io.localizable.demo.sdk.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class FileLoader<T extends Serializable> {
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
   * Delete the file in the Localizable Folder
   *
   */
  public boolean delete() {
    try {
      File file = fileFromFilename(name, context);
      if (file.exists()) {
        return file.delete();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
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
