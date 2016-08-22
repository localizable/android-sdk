package io.localizable.uploader.model

import java.io.File
import java.io.Serializable

data class LocalizedString(val key: String, var value: String? = null, var translatable: Boolean = true): Serializable

data class LanguageStrings(val language: String, val strings: List<LocalizedString>): Serializable {

  fun languageStringsByRemovingRepeated(oldStrings: List<LanguageStrings>): LanguageStrings {
    val newStrings: MutableList<LocalizedString> = mutableListOf()

    strings.map { string ->
      val numberOfAppearancesInOldString = oldStrings.filter { oldStrings ->
        oldStrings.strings.contains(string)
      }
      if (numberOfAppearancesInOldString.count() == 0) {
        newStrings.add(string)
      }
    }
    return LanguageStrings(language, newStrings.toList())
  }

  fun languageStringsByRemovingRepeatedKeys(oldStrings: List<LanguageStrings>): LanguageStrings {
    val oldStringsKeys = oldStrings.map { it.strings.map { string -> string.key } }
    val newStrings: MutableList<LocalizedString> = mutableListOf()

    strings.map { string ->
      val numberOfAppearancesInOldString = oldStringsKeys.filter { oldStrings ->
        oldStrings.contains(string.key)
      }
      if (numberOfAppearancesInOldString.count() == 0) {
        newStrings.add(string)
      }
    }
    return LanguageStrings(language, newStrings.toList())
  }
}

class ResourceFolder(val folder: File, resourceLanguage: String? = null): Comparable<ResourceFolder>, Serializable {
    val language: String

    init {
        language = if (resourceLanguage.isNullOrEmpty()) { "base" } else { resourceLanguage!! }
    }

    override fun compareTo(other: ResourceFolder): Int {
        return folder.compareTo(other.folder)
    }
}
