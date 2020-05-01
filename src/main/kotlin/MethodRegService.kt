import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project

class MethodRegService(project: Project?) {
    companion object {
        fun getInstance(project: Project): MethodRegService {
            return ServiceManager.getService(project, MethodRegService::class.java)
        }
    }

    private var methodsToMark: Map<String, MethodToMark> = mapOf()

    fun updateMarkedMethods(methods: Map<String, MethodToMark>) {
        methodsToMark = methods
    }

    fun getMarkedMethods(): Map<String, MethodToMark> {
        return methodsToMark
    }

}