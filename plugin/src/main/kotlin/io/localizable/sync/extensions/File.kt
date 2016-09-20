package io.localizable.sync.extensions

import org.xml.sax.helpers.DefaultHandler
import java.io.File
import javax.xml.parsers.SAXParserFactory

fun<T: DefaultHandler> File.loadXMLFileWithHandler(handler: T): T? {
  try {
    val saxParserFactory = SAXParserFactory.newInstance()
    val saxParser = saxParserFactory.newSAXParser()
    saxParser.parse(this, handler)
    return handler
  } catch (exception: Exception) {
    exception.printStackTrace()
    return null
  }
}