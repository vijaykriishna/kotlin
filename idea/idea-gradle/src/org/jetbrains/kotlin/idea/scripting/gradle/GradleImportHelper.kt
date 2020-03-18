/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("UnstableApiUsage")

package org.jetbrains.kotlin.idea.scripting.gradle

import com.intellij.notification.*
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.UserDataProperty
import org.jetbrains.plugins.gradle.settings.GradleProjectSettings
import org.jetbrains.plugins.gradle.util.GradleConstants

fun runPartialGradleImport(project: Project) {
    // TODO: partial import
    val gradleSettings = ExternalSystemApiUtil.getSettings(project, GradleConstants.SYSTEM_ID)
    val projectSettings = gradleSettings.getLinkedProjectsSettings()
        .filterIsInstance<GradleProjectSettings>()
        .firstOrNull() ?: return

    ExternalSystemUtil.refreshProject(
        projectSettings.externalProjectPath,
        ImportSpecBuilder(project, GradleConstants.SYSTEM_ID)
            .build()
    )
}

private var Project.notificationPanel: Notification?
        by UserDataProperty<Project, Notification>(Key.create("load.script.configuration.panel"))


fun showNotificationForProjectImport(project: Project, callback: () -> Unit) {
    if (project.notificationPanel != null) return

    val notification = getNotificationGroup().createNotification(
        "Script configuration may be changed. If you want to reload them, run `Sync Project with Gradle files`",
        NotificationType.INFORMATION
    )
    notification.addAction(NotificationAction.createSimple("Sync Project with Gradle files") {
        callback()
    })
    notification.addAction(NotificationAction.createSimple("Hide") {
        notification.hideBalloon()
    })
    project.notificationPanel = notification
    notification.notify(project)
}

fun hideNotificationForProjectImport(project: Project): Boolean {
    if (project.notificationPanel == null) return false
    project.notificationPanel?.expire()
    return true
}

private fun getNotificationGroup(): NotificationGroup {
    return NotificationGroup.findRegisteredGroup("Kotlin script configurations")
        ?: NotificationGroup("Kotlin script configurations", NotificationDisplayType.STICKY_BALLOON, true)
}