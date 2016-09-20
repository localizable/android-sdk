package io.localizable.sync.model

class UploadStringsRequestBody(val languages: List<LanguageDelta>, val app: App, val platform: String = "Android")