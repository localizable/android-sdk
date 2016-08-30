package io.localizable.uploader.model

class LanguageDelta(languageCode: String, updatedStrings: List<LocalizedString>, removedStrings: List<LocalizedString>) {

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
    private fun mergeLanguages(currentStrings: List<LanguageStrings>, cachedStrings: List<LanguageStrings>):
        List<Triple<String, List<LocalizedString>, List<LocalizedString>>> {
      val tmpCurrentStrings = currentStrings.toMutableList()
      val tmpCachedStrings = cachedStrings.toMutableList()
      val result = mutableListOf<Triple<String, List<LocalizedString>, List<LocalizedString>>>()

      tmpCurrentStrings.forEach { currentItem ->
        val language = currentItem.language
        val current = currentItem.strings
        val cached = mutableListOf<LocalizedString>()
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

    fun deltasFromLanguages(currentStrings: List<LanguageStrings>, cachedStrings: List<LanguageStrings>): List<LanguageDelta> {
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
