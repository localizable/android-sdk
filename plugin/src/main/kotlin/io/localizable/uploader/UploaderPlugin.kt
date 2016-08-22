package io.localizable.uploader

import org.gradle.api.Plugin
import org.gradle.api.Project

class UploaderPlugin: Plugin<Project> {
    override fun apply(project: Project?) {
        project?.let { ProjectHandler(it).createTasks() }
    }
}