package services

import Inspection
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.KtElement

class CommandsRegService(project: Project?) {
    companion object {
        fun getInstance(project: Project): CommandsRegService {
            return ServiceManager.getService(project, CommandsRegService::class.java)
        }
    }

    private var commands: Map<String, Any> = mapOf()

    fun getInspections(): Set<Inspection<in KtElement>> {
        @Suppress("UNCHECKED_CAST")

        (return if (this.commands["inspections"] != null) {
            this.commands["inspections"] as Set<Inspection<in KtElement>>
        } else {
            emptySet()
        })
    }

    fun updateCommands(commands: Map<String, Any>) {
        this.commands = commands
    }

    fun getMarkedMethods(): Map<String, Any> {
        return commands
    }

}