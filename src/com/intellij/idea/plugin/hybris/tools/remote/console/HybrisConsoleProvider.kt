package com.intellij.idea.plugin.hybris.tools.remote.console

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import javax.swing.Icon

interface HybrisConsoleProvider {

    val tabTitle: String
        get() = ""

    val tip: String
        get() = ""

    val icon: Icon?
        get() = null

    fun createConsole(project: Project): HybrisConsole?

    companion object {
        val EP_NAME = ExtensionPointName.create<HybrisConsoleProvider>("com.intellij.idea.plugin.hybris.consoleProvider")
    }
}
