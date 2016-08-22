package io.localizable.uploader.extensions

import org.gradle.api.Project
import java.io.File

/**
 * Created by bruno on 22/08/16.
 */

//Returns an array of Folders that contains values -> /res/values
fun Project.resourcesFolders(): List<File> {
    return valuesFolder(projectDir)
}

private fun valuesFolder(file: File): List<File> {
    if (file.isDirectory && file.path.contains("/src/")  && file.name.startsWith("values", ignoreCase = true)) {
        return listOf(file)
    } else if (file.isDirectory) {
        return file.listFiles().map { valuesFolder(it) }.flatten()
    } else {
        return listOf()
    }
}