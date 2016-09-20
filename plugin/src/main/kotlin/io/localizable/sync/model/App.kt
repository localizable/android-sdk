package io.localizable.sync.model

import io.localizable.sync.xml.ManifestFileHandler
import io.localizable.sync.model.LocalizedString

data class App(val name: String?, val bundle: String?,  val version: String?, val build: Int?) {
    companion object {
        fun createFromManifest(manifest: ManifestFileHandler, defaultStrings: List<io.localizable.sync.model.LocalizedString>?): App {
            return App(name = manifest.appNameWithStrings(defaultStrings),
                bundle = manifest.applicationPackage,
                version = manifest.appVersionName, build = manifest.appVersionInt)
        }
    }
}