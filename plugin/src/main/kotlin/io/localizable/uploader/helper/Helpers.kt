package io.localizable.uploader.helper

import io.localizable.uploader.model.LanguageStrings
import io.localizable.uploader.model.ResourceFolder
import io.localizable.uploader.xml.ResourcesFileHandler
import javax.xml.parsers.SAXParserFactory


class LocalizableHelper {
    companion object {
        fun loadStringsFromFolder(valuesFolder: ResourceFolder): LanguageStrings {
            val strings = valuesFolder.folder.listFiles().map { file ->
                if (file.name.endsWith(".xml")) {
                    try {
                        val saxParserFactory = SAXParserFactory.newInstance()
                        val saxParser = saxParserFactory.newSAXParser()
                        val handler = ResourcesFileHandler()
                        saxParser.parse(file, handler)
                        return@map handler.localizedStrings.toList()
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        return@map null
                    }
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
