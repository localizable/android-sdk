package io.localizable.sync.model

import io.localizable.sync.model.LocalizedString
import java.io.Serializable


data class LanguageStrings(val language: String, val strings: List<io.localizable.sync.model.LocalizedString>): Serializable