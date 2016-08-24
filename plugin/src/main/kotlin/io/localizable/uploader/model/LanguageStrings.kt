package io.localizable.uploader.model

import java.io.Serializable


data class LanguageStrings(val language: String, val strings: List<LocalizedString>): Serializable