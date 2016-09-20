package io.localizable.sync.helper

import io.localizable.sync.extensions.loadXMLFileWithHandler
import io.localizable.sync.model.LanguageStrings
import io.localizable.sync.model.ResourceFolder
import io.localizable.sync.xml.ManifestFileHandler
import io.localizable.sync.xml.ResourcesFileHandler
import org.gradle.api.Project
import java.io.File

class LocalizableHelper {
  companion object {

    fun manifestFileForTarget(project: Project, buildName: String, flavorName: String): ManifestFileHandler? {
      val intermediateXML = File("${project.projectDir.absolutePath}/build/intermediates/manifests/full/$flavorName/$buildName/AndroidManifest.xml")
      val staticXML = File("${project.projectDir.absolutePath}/src/main/AndroidManifest.xml")

      if (intermediateXML.exists()) {
        return intermediateXML.loadXMLFileWithHandler(ManifestFileHandler())
      } else if (staticXML.exists()) {
        return staticXML.loadXMLFileWithHandler(ManifestFileHandler())
      } else {
        return null
      }
    }

    fun loadStringsFromFolder(valuesFolder: ResourceFolder): LanguageStrings {
      val strings = valuesFolder.folder.listFiles().map { file ->
        if (file.name.endsWith(".xml")) {
          return@map file.loadXMLFileWithHandler(ResourcesFileHandler())?.localizedStrings?.toList()
        } else {
          return@map null
        }
      }
          .filterNotNull()
          .flatten()
      return LanguageStrings(language = valuesFolder.language, strings = strings)
    }

    fun taskAffix(buildName: String , flavorName: String ): String {
      return if (flavorName.isNotEmpty()) {
        "${flavorName.capitalize()}${buildName.capitalize()}"
      } else {
        buildName.capitalize()
      }
    }
  }
}
