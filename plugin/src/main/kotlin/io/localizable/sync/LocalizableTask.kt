package io.localizable.sync

import io.localizable.sync.extensions.resourcesFolders
import io.localizable.sync.model.App
import io.localizable.sync.model.LanguageDelta
import io.localizable.sync.model.LanguageStrings
import io.localizable.sync.model.ResourceFolder
import io.localizable.sync.helper.LocalizableHelper
import io.localizable.sync.helper.loadFile
import io.localizable.sync.helper.storeToFile
import io.localizable.sync.model.LocalizedString
import io.localizable.sync.model.UploadStringsRequestBody
import io.localizable.sync.networking.Network
import io.localizable.sync.xml.ManifestFileHandler
import org.gradle.api.Project
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class LocalizableTask(val project: Project, val manifest: ManifestFileHandler, val taskName: String) {

  private val valueFiles: List<ResourceFolder> by lazy {
    project.resourcesFolders()
        .map { folder ->
          val language = folder.name.replace("values", "").split("-").reduce { acc, current ->
            //Android have this language format pt-rBR-vH1 for country and language
            if (current.startsWith("v") || current.startsWith("r")) {
              val nCurrent = current.removeRange(0,1)
              "$acc-$nCurrent"
            } else {
              current
            }
          }
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

  private val projectBaseStrings: List<LocalizedString>? by lazy {
    projectStringsByLanguage
        .filter { it.language == ResourceFolder.defaultResourceFolder }
        .firstOrNull()
        ?.strings
  }

  private val localizedDeltas: List<LanguageDelta> by lazy {
    LanguageDelta.deltasFromLanguages(projectStringsByLanguage, alreadyUpdatedStringsByLanguage)
  }

  private val localizableApp: App by lazy {
    App.createFromManifest(manifest, projectBaseStrings)
  }

  private val localizableServiceObject: UploadStringsRequestBody by lazy {
    UploadStringsRequestBody(localizedDeltas, localizableApp)
  }

  fun syncResources(): Boolean {
    if (localizedDeltas.isEmpty()) {
      return false
    }

    try {
      Network.service.UploadLanguages(manifest.localizableToken, localizableServiceObject).enqueue(object : Callback<Void> {
        override fun onFailure(call: Call<Void>?, t: Throwable?) {
          println("Error updating Localizable Strings")
          t?.printStackTrace()
        }

        override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
          response?.let {
            if (it.isSuccessful) {
              cacheStringsByLanguage()
            } else {
              println("Error contacting Localizable Server ${it.headers()}")
            }
          }
        }
      })
    } catch (e: Exception) {
      e.printStackTrace()
    } finally {
      return true
    }
  }

  private fun cacheStringsByLanguage() {
    storeToFile(tmpFilePath, projectStringsByLanguage)
  }
}
