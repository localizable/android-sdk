package io.localizable.uploader

import io.localizable.uploader.extensions.resourcesFolders
import io.localizable.uploader.helper.LocalizableHelper
import io.localizable.uploader.helper.loadFile
import io.localizable.uploader.helper.storeToFile
import io.localizable.uploader.model.LanguageStrings
import io.localizable.uploader.model.ResourceFolder
import org.gradle.api.Project
import java.io.File

class LocalizableTask(val project: Project, val taskName: String) {

  private val valueFiles: List<ResourceFolder> by lazy {
    project.resourcesFolders()
        .map { folder ->
          val language = folder.name.replace("values", "").replace("-", "")
          ResourceFolder(folder, language)
        }
  }




  private val tmpDirectory: String by lazy {
    val path = "${project.projectDir.absolutePath}/build/intermediates/Localizable/$taskName"
    val file = File(path)
    if (!file.exists()) {
      file.mkdirs()
    }
    path
  }

  private val tmpFilePath: String by lazy {
    val fileName = "tmpValues.ser"
    val file = File(tmpDirectory, fileName)
    if (!file.exists()) {
      file.createNewFile()
    }
    file.absolutePath
  }

  private val alreadyUpdatedStringsByLanguage: List<LanguageStrings> by lazy {
    loadFile(tmpFilePath)
  }

  private val projectStringsByLanguage: List<LanguageStrings> by lazy {
    valueFiles
        .map { LocalizableHelper.loadStringsFromFolder(it) }
        .filter { it.strings.count() > 0 }
  }

  private val stringUpdates: List<LanguageStrings> by lazy {
    projectStringsByLanguage.map { projectStrings ->
      projectStrings.languageStringsByRemovingRepeated(alreadyUpdatedStringsByLanguage)
    }.filter { it.strings.count() > 0 }
  }

  private val stringRemovals: List<LanguageStrings> by lazy {
    alreadyUpdatedStringsByLanguage.map { projectStrings ->
      projectStrings.languageStringsByRemovingRepeatedKeys(projectStringsByLanguage)
    }.filter { it.strings.count() > 0 }
  }

  fun syncResources() {
    if (stringUpdates.count() == 0 && stringRemovals.count() == 0) {
      println("UP-TO-DATE")
      return
    }

    stringUpdates.forEach {
      println("UPDATE -> $it")
    }

    stringRemovals.forEach {
      println("REMOVAL -> $it")
    }

    cacheStringsByLanguage()
  }

  private fun cacheStringsByLanguage() {
    storeToFile(tmpFilePath, projectStringsByLanguage)
  }

}
