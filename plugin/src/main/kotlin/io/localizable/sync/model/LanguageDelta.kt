package io.localizable.sync.model

import io.localizable.sync.model.LanguageStrings
import io.localizable.sync.model.LocalizedString

class LanguageDelta(languageCode: String, updatedStrings: List<io.localizable.sync.model.LocalizedString>, removedStrings: List<io.localizable.sync.model.LocalizedString>) {

  var code: String
  var update: Map<String, String>
  var remove: List<String>

  init {
    this.code = languageCode
    this.remove = removedStrings.map { it.key }
    val map: MutableMap<String, String> = mutableMapOf()
    updatedStrings.forEach { localizedString ->
      localizedString .value?.let { value ->
        map[localizedString .key] = value
      }
    }
    this.update = map
  }

  companion object {
    private fun mergeLanguages(currentStrings: List<io.localizable.sync.model.LanguageStrings>, cachedStrings: List<io.localizable.sync.model.LanguageStrings>):
        List<Triple<String, List<io.localizable.sync.model.LocalizedString>, List<io.localizable.sync.model.LocalizedString>>> {
      val tmpCurrentStrings = currentStrings.toMutableList()
      val tmpCachedStrings = cachedStrings.toMutableList()
      val result = mutableListOf<Triple<String, List<io.localizable.sync.model.LocalizedString>, List<io.localizable.sync.model.LocalizedString>>>()

      tmpCurrentStrings.forEach { currentItem ->
        val language = currentItem.language
        val current = currentItem.strings
        val cached = mutableListOf<io.localizable.sync.model.LocalizedString>()
        val matches = tmpCachedStrings.filter { it.language == language }
        matches.forEach {
          cached.addAll(it.strings)
          tmpCachedStrings.remove(it)
        }
        result.add(Triple(language, current, cached))
      }

      tmpCachedStrings.forEach {
        result.add(Triple(it.language, listOf(), it.strings))
      }

      return result
    }

    fun deltasFromLanguages(currentStrings: List<io.localizable.sync.model.LanguageStrings>, cachedStrings: List<io.localizable.sync.model.LanguageStrings>): List<LanguageDelta> {
      val mergedLanguages = mergeLanguages(currentStrings, cachedStrings)

      val result = mutableListOf<LanguageDelta>()

      mergedLanguages.forEach { mergedLanguage ->
        val addedStrings = mergedLanguage.second.filter { newString ->
          !mergedLanguage.third.contains(newString)
        }

        val removedStrings = mergedLanguage.third.filter { cachedString ->
          !mergedLanguage.second.map { it.key }.contains(cachedString.key)
        }

        if (addedStrings.isNotEmpty() || removedStrings.isNotEmpty()) {
          result.add(LanguageDelta(mergedLanguage.first, addedStrings, removedStrings))
        }
      }

      return result
    }
  }
}
