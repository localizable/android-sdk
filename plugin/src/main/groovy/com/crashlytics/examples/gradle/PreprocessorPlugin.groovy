package com.crashlytics.examples.gradle

import com.google.common.io.Files
import groovy.json.JsonOutput
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.maven.model.Build
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.Task;

class PreprocessorPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.configure(project) {
            if (it.hasProperty("android")) {
                tasks.whenTaskAdded { task ->
                    taskAdded(task, project)
                }
            }
        }
    }

    void taskAdded(Task task, Project project) {
        // Returns an empty list if the plugin only has the default flavor
        // But we still need something to iterate over, so let’s make an empty flavor.
        def projectFlavorNames = project.("android").productFlavors.collect { it.name }
        projectFlavorNames = projectFlavorNames.size() != 0 ? projectFlavorNames : [""]

        project.("android").buildTypes.all { build ->
            def buildName = build.name.toString()
            for (flavorName in projectFlavorNames) {
                processBuild(buildName, flavorName, task, project)
            }
        }
    }

    void processBuild(String buildName, String flavorName, Task task, Project project) {
        def taskAffix = taskAffix(buildName, flavorName)

        def processTask = "process${taskAffix}Resources".toString()

        // Only add task when processAppResources is being added
        // Will make processAppResources depend on localizableApp and localizableApp depend on
        // mergeAppResources.
        if (processTask.equals(task.name.toString())) {
            def yourTaskName = "localizable${taskAffix}"
            project.task(yourTaskName) << {
                try {
                    Thread.start {
                        executeLocalizable(project)
                    }
                } catch (Throwable t) {
                    t.printStackTrace()
                }
            }
            task.dependsOn(yourTaskName)
            def mergeTask = "merge${taskAffix}Resources".toString()
            project.(yourTaskName.toString()).dependsOn(mergeTask)
        }
    }

    String taskAffix(String buildName, String flavorName) {
        if (!"".equals(flavorName)) {
            return "${flavorName.capitalize()}${buildName.capitalize()}"
        } else {
            // If we are working with the empty flavor, there’s no second affix
            return "${buildName.capitalize()}"
        }
    }

    void executeLocalizable(Project project) {
        def languages = []
        project.getProjectDir().eachFileRecurse { file ->
            if (file.name.equals("strings.xml")) {
                def folder = file.parentFile
                def language = folder.name.replace("values", "").replace("-", "")
                language = language.length() == 0 ? "default" : language
                languages.add(processLanguage(project, language, file))
                saveLanguageFile(project, language, file)
            }
        }
        def json = JsonOutput.toJson(["languages": languages])
        uploadStrings(json)
    }

    Map processLanguage(Project project, String language, File stringsFile) {
        def strings = stringsFromFile(stringsFile)

        def updatedStrings = [:]
        def deletedStrings = []

        def oldLanguageFile = loadLanguageFile(project, language)
        if (oldLanguageFile == null) {
            updatedStrings = strings
        } else {
            Map<String, String> oldStrings = stringsFromFile(oldLanguageFile)

            // Detect deleted tokens
            for (String token in oldStrings.keySet()) {
                if (!strings.containsKey(token)) {
                    deletedStrings.add(token)
                }
            }

            // Detect updated or created tokens
            for (String token in strings.keySet()) {
                def newString = strings[token]
                def oldString = oldStrings[token]

                if (newString != oldString) {
                    updatedStrings[token] = newString
                }
            }

        }
        return ["code": language, "update": updatedStrings, "remove": deletedStrings]
    }

    Map<String, String> stringsFromFile(File stringsFile) {
        def result = new XmlSlurper().parse(stringsFile)

        def localizableStrings = [:]
        for (string in result.string) {
            def token = string.@'name'.toString()
            localizableStrings[token] = string.toString()
        }
        return localizableStrings
    }

    File loadLanguageFile(Project project, String language) {
        def languageFile = new File("${project.getProjectDir().absolutePath}/build/intermediates/localizable/${language}_strings.xml")
        return languageFile.exists() ? languageFile : null
    }

    void saveLanguageFile(Project project, String language, File stringsFile) {
        def localizableLanguageFolder = new File("${project.getProjectDir().absolutePath}/build/intermediates/localizable/")
        localizableLanguageFolder.mkdirs()
        def localizableStringsFile = new File("${project.getProjectDir().absolutePath}/build/intermediates/localizable/${language}_strings.xml")
        Files.copy(stringsFile, localizableStringsFile)
    }

    void uploadStrings(String json) {
        def http = new HTTPBuilder("http://192.168.0.161:4000/api/v1/languages")
        http.request(Method.POST, ContentType.JSON) {
            body = json
            headers.'X-Localizable-Token' = "O marcelo nao poe isto a funcionar"
            response.success = { resp ->
                println "Success! ${resp.status}"
            }

            response.failure = { resp ->
                println "Request failed with status ${resp.status}"
            }
        }
    }

}
