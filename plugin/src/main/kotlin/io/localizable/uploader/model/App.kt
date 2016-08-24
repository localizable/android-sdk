package io.localizable.uploader.model

import io.localizable.uploader.xml.ManifestFileHandler

data class App(val name: String?, val bundle: String?,  val version: String?, val build: Int?) {
    companion object {
        fun createFromManifest(manifest: ManifestFileHandler, defaultStrings: List<LocalizedString>?): App {
            return App(name = manifest.appNameWithStrings(defaultStrings),
                bundle = manifest.applicationPackage,
                version = manifest.appVersionName, build = manifest.appVersionInt)
        }
    }
}