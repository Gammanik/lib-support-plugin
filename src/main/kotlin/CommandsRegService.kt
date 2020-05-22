import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project

class CommandsRegService(project: Project?) {
    companion object {
        fun getInstance(project: Project): CommandsRegService {
            return ServiceManager.getService(project, CommandsRegService::class.java)
        }
    }

    private var commands: Map<String, Any> = mapOf()

    fun updateMarkedMethods(methods: Map<String, Any>) {
//        this.commands["rr"] = methods
    }

    fun updateCommands(commands: Map<String, Any>) {
        this.commands = commands
    }

    fun getMarkedMethods(): Map<String, Any> {
        return commands
    }

}