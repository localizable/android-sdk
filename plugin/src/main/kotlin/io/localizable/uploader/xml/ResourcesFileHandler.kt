package io.localizable.uploader.xml

import io.localizable.uploader.model.LocalizedString
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

class ResourcesFileHandler: DefaultHandler() {

    var localizedStrings: MutableList<LocalizedString> = mutableListOf()
    private var localizedString: LocalizedString? = null

    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {

        qName?.let {
            when (it) {
                "string" -> {
                    attributes?.getValue("name")?.let { key ->
                        val translatable = attributes.getValue("translatable")?.toBoolean() ?: true
                        localizedString = LocalizedString(key, translatable = translatable)
                    }
                }
                else -> localizedString = null
            }
        }
    }

    override fun characters(ch: CharArray?, start: Int, length: Int) {
        ch?.let { characters ->
            localizedString?.let {
                it.value = String(characters, start, length)
            }
        }
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        qName?.let {
            when (it) {
                "string" -> {
                    localizedString?.value?.let {
                        localizedStrings.add(localizedString!!)
                        localizedString = null
                    }
                }
                else -> {}
            }
        }
    }
}
