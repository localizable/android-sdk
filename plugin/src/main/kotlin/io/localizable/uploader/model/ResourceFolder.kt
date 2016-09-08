package io.localizable.uploader.model

import java.io.File
import java.io.Serializable

class ResourceFolder(val folder: File, resourceLanguage: String? = null): Comparable<ResourceFolder>, Serializable {

  companion object {
    val defaultResourceFolder = "default"
  }

  val language: String

  init {
    language = if (resourceLanguage.isNullOrEmpty()) { defaultResourceFolder } else { resourceLanguage!! }
  }

  override fun compareTo(other: ResourceFolder): Int {
    return folder.compareTo(other.folder)
  }
}