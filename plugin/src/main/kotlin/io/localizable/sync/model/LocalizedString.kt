package io.localizable.sync.model

import java.io.Serializable

data class LocalizedString(val key: String, var value: String? = null, var translatable: Boolean = true): Serializable
