package io.localizable.uploader.helper

import io.localizable.uploader.model.LanguageStrings
import java.io.*

@Suppress("UNCHECKED_CAST")
fun loadFile(path: String): List<LanguageStrings> {
  try {
    val file = File(path)
    if (!file.exists() || !file.isFile) {
      return listOf()
    }
    val fileInputStream = FileInputStream(file)
    val objectInputStream = ObjectInputStream(fileInputStream)
    val returnValue = objectInputStream.readObject()  as? List<LanguageStrings> ?: listOf()
    fileInputStream.close()
    objectInputStream.close()
    return returnValue
  } catch (exception: Exception) {
    return listOf()
  }
}

fun storeToFile(path: String, file: List<LanguageStrings>) {
  try {
    val fileStream = FileOutputStream(path)
    val os = ObjectOutputStream(fileStream)
    os.writeObject(file)
    os.close()
  }catch (exception: Exception) {
  }
}