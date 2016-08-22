package io.localizable.uploader

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.dsl.BuildType
import io.localizable.uploader.helper.LocalizableHelper
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer

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

    private fun createLocalizableFromTask(task: Task, taskAffix: String) {
        val localizableTaskName = "localizable$taskAffix"
        val nTask = project.task(localizableTaskName)

        nTask.doFirst {
            LocalizableTask(project, localizableTaskName).syncResources()
        }

        task.dependsOn(nTask)
        val mergeTask = "merge${taskAffix}Resources"
        project.tasks.getByName(localizableTaskName).dependsOn(mergeTask)
    }

    private fun processBuild(buildName: String, flavorName: String, task: Task) {
        val taskAffix = LocalizableHelper.taskAffix(buildName, flavorName)
        val processTask = "process${taskAffix}Resources"

        if (processTask.equals(task.name)) {
            createLocalizableFromTask(task, taskAffix)
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











