package actions

import com.intellij.codeInspection.LocalInspectionEP
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.extensions.*
import com.intellij.openapi.project.ProjectManager


class AddInspectionAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val ep = LocalInspectionEP()
        ep.pluginDescriptor = PluginManager.getPlugin(PluginId.getId("org.example.lib-support"))!!
        ep.language = "kotlin"
        ep.implementationClass = "MyAbstractInspection"
        ep.displayName = "someEx"
        ep.groupBundle = "messages.InspectionsBundle"
        ep.groupKey = "group.names.probable.bugs"
        ep.level = "ERROR"
        ep.enabledByDefault = true

        LocalInspectionEP.LOCAL_INSPECTION.getPoint(ApplicationManager.getApplication())
            .registerExtension(ep)
    }
}
