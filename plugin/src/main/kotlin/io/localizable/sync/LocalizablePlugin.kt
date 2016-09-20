package io.localizable.sync

import org.gradle.api.Plugin
import org.gradle.api.Project

class LocalizablePlugin : Plugin<Project> {
    override fun apply(project: Project?) {
        project?.let { ProjectHandler(it).createTasks() }
    }
}