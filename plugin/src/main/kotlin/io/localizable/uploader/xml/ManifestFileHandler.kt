package io.localizable.uploader.xml

import io.localizable.uploader.model.LocalizedString
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

class ManifestFileHandler: DefaultHandler() {

    var applicationPackage: String? = null
    var localizableToken: String? = null
    var appName: String? = null
    var appNameIsResource = false
    var appVersionInt: Int? = null
    var appVersionName: String? = null


    fun appNameWithStrings(baseStrings: List<LocalizedString>?): String? {
      if (!appNameIsResource) {
        return appName
      }
      val appNameKey = appName?.split("string/")?.last() ?: return appName
      val appNameFromStrings = baseStrings?.firstOrNull { it.key.equals(appNameKey, true) }?.value
      return appNameFromStrings ?: appName
    }

    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {

        qName?.let { element ->
            when (element) {
                "manifest" -> {
                    applicationPackage = attributes?.getValue("package")
                    appVersionName = attributes?.getValue("android:versionName")
                    appVersionInt =  attributes?.getValue("android:versionCode")?.toInt()
                }
                "application" -> {
                    attributes?.getValue("android:label")?.let {
                        appName = it
                        appNameIsResource = it.startsWith("@string/")
                    }

                }
                "meta-data" -> {
                    attributes?.getValue("android:name")?.let { metadataName ->
                        if (metadataName == "io.localizable.SDK_TOKEN") {
                            localizableToken = attributes.getValue("android:value")
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
