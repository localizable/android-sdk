package io.localizable.uploader

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.dsl.BuildType
import io.localizable.uploader.helper.LocalizableHelper
import io.localizable.uploader.xml.ManifestFileHandler
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskOutputs

class ProjectHandler(val project: Project) {

    private val isAndroidProject: Boolean
        get() {
            return project.plugins.hasPlugin(AppPlugin::class.java)
        }

    private val appExtension: AppExtension
        get() {
            return  project.extensions.getByType(AppExtension::class.java)
        }

    private val flavorNames: List<String> by lazy {
        val configs = appExtension.productFlavors.map { it.name }
        if (configs.isNotEmpty()) configs else listOf("")
    }

    private val buildTypes: Collection<BuildType> by lazy { appExtension.buildTypes }

    private val tasks: TaskContainer by lazy { project.tasks }

    fun createTasks() {

        if (!isAndroidProject) {
            throw Throwable("You should apply android to your project first")
        }

        tasks.whenTaskAdded { handleTask(it) }
    }

    private fun createLocalizableFromTask(task: Task, taskAffix: String, buildName: String, flavorName: String) {
        val localizableTaskName = "localizable$taskAffix"
        val nTask = project.task(localizableTaskName)

        nTask.doFirst {
            val manifest = LocalizableHelper.manifestFileForTarget(project, buildName, flavorName)
            if (manifest == null) {
                println("Could not find any manifest file")
              return@doFirst
            }

          nTask.didWork = LocalizableTask(project, manifest, localizableTaskName).syncResources()
        }

        task.dependsOn(nTask)
        val mergeTask = "merge${taskAffix}Resources"
        project.tasks.getByName(localizableTaskName).dependsOn(mergeTask)
    }

    private fun processBuild(buildName: String, flavorName: String, task: Task) {
        val taskAffix = LocalizableHelper.taskAffix(buildName, flavorName)
        val processTask = "process${taskAffix}Resources"

        if (processTask.equals(task.name)) {
            createLocalizableFromTask(task, taskAffix, buildName, flavorName)
        }
    }

    private fun handleTask(task: Task) {
        buildTypes.forEach { build ->
            flavorNames.forEach { flavor ->
                processBuild(build.name, flavor, task)
            }
        }
    }
}
