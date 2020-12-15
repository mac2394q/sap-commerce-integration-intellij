package com.intellij.idea.plugin.hybris.tools.remote.console.view

import com.intellij.idea.plugin.hybris.common.utils.HybrisIcons
import com.intellij.idea.plugin.hybris.tools.remote.console.*
import com.intellij.idea.plugin.hybris.tools.remote.console.actions.*
import com.intellij.idea.plugin.hybris.tools.remote.console.actions.handler.HybrisConsoleExecuteActionHandler
import com.intellij.idea.plugin.hybris.tools.remote.console.actions.handler.HybrisConsoleExecuteValidateActionHandler
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.JBTabsPaneImpl
import icons.JetgroovyIcons.Groovy.Groovy_16x16
import java.awt.BorderLayout
import javax.swing.Icon
import javax.swing.JPanel
import javax.swing.SwingConstants.TOP


/**
 * @author Nosov Aleksandr <nosovae.dev@gmail.com>
 */
class HybrisConsolePanel(val project: Project) : SimpleToolWindowPanel(true), Disposable {

    override fun dispose() {

    }

    private val impexConsole = HybrisImpexConsole(project)
    private val groovyConsole = HybrisGroovyConsole(project)
    private val monitorConsole = HybrisImpexMonitorConsole(project)

    private val actionToolbar: ActionToolbar
    private val hybrisTabs: HybrisTabs
    init {
        layout = BorderLayout()

        val toolbarActions = DefaultActionGroup()
        val actionManager = ActionManager.getInstance()
        actionToolbar = actionManager.createActionToolbar(ActionPlaces.UNKNOWN, toolbarActions, false)

        val panel = JPanel(BorderLayout())

        hybrisTabs = HybrisTabs(impexConsole,
                groovyConsole,
                monitorConsole,
                project, TOP)

        panel.add(hybrisTabs.component, BorderLayout.CENTER)
        actionToolbar.setTargetComponent(hybrisTabs.component)
        panel.add(actionToolbar.component, BorderLayout.WEST)

        val actionHandler = HybrisConsoleExecuteActionHandler(project, false)
        val validateHandler = HybrisConsoleExecuteValidateActionHandler(project, false)
        val executeAction = HybrisExecuteImmediatelyAction(hybrisTabs, actionHandler)
        executeAction.registerCustomShortcutSet(CommonShortcuts.ALT_ENTER, this.component)

        val choseInstanceAction = HybrisChooseInstanceAction()

        toolbarActions.add(choseInstanceAction)
        toolbarActions.add(executeAction)
        toolbarActions.add(HybrisSuspendAction(hybrisTabs, actionHandler))
        toolbarActions.add(HybrisImpexValidateAction(hybrisTabs, validateHandler))

        val actions = impexConsole.createConsoleActions()
        actions[5] = HybrisClearAllAction(hybrisTabs)
        toolbarActions.addAll(*actions)
        add(panel)
    }

    override fun getComponent() = super.getComponent()!!

    fun sendTextToConsole(console: HybrisConsole, text: String) {
        console.setInputText(text)
    }

    fun setActiveConsole(console: HybrisConsole) {
        hybrisTabs.setActiveConsole(console)
    }

    fun getActiveConsole() : HybrisConsole {
        return hybrisTabs.activeConsole()
    }

    fun findConsole(consoleTitle: String): HybrisConsole? {
        for (index in 0 until hybrisTabs.tabCount) {
            val component = hybrisTabs.getComponentAt(index) as HybrisConsole
            if (component.title == consoleTitle) {
                return component
            }
        }
        return null
    }

    fun validateImpex() {
        val action = actionToolbar.actions.first { it is HybrisImpexValidateAction }
        val event = AnActionEvent.createFromDataContext("unknown", action.templatePresentation, actionToolbar.toolbarDataContext)
        action.actionPerformed(event)
    }

    fun execute() {
        val action = actionToolbar.actions.first { it is HybrisExecuteImmediatelyAction }
        val event = AnActionEvent.createFromDataContext("unknown", action.templatePresentation, actionToolbar.toolbarDataContext)
        action.actionPerformed(event)
    }
}

class HybrisTabs(impexConsole: HybrisImpexConsole,
                 groovyConsole: HybrisGroovyConsole,
                 impexMonitorConsole: HybrisImpexMonitorConsole,
                 project: Project,
                 tabPlacement: Int) : JBTabsPaneImpl(project, tabPlacement, Disposable { }) {

    private val consoles = arrayListOf<HybrisConsole>()

    init {
        addConsoleTab("Impex", HybrisIcons.IMPEX_FILE, impexConsole, "Impex Console")
        addConsoleTab("Groovy Scripting", Groovy_16x16, groovyConsole, "Groovy Console")
        addConsoleTab("Impex Monitor", HybrisIcons.TYPE_SYSTEM, impexMonitorConsole, "Last imported Impex files")

        for (extension in HybrisConsoleProvider.EP_NAME.extensions) {
            val console = extension.createConsole(project)

            if (console != null) {
                addConsoleTab(extension.tabTitle, extension.icon, console, extension.tip)
            }
        }
    }

    private fun addConsoleTab(title: String, icon: Icon?, console: HybrisConsole, tip: String) {
        insertTab(title, icon, console.component, tip, consoles.size)
        consoles.add(console)
    }

    fun activeConsole() = consoles[selectedIndex]
    fun setActiveConsole(console: HybrisConsole) {
        selectedIndex=consoles.indexOf(console);
    }
}